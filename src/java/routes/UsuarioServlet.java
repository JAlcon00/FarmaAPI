package routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.UsuarioController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AuditLog;
import model.Usuario;
import model.RefreshToken;
import services.AuditService;
import services.RefreshTokenService;
import utils.JsonResponse;

@WebServlet("/api/usuarios/*")
public class UsuarioServlet extends HttpServlet {
    
    private UsuarioController usuarioController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioController = new UsuarioController();
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        setCORSHeaders(response);
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/usuarios - Obtener todos los usuarios
                List<Usuario> usuarios = usuarioController.getAllUsuarios();
                JsonResponse.success(response, usuarios);
                
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    String segment = pathParts[1];
                    
                    // GET /api/usuarios/me - Obtener usuario actual desde token JWT
                    if ("me".equals(segment)) {
                        Object userIdObj = request.getAttribute("userId");
                        if (userIdObj != null) {
                            try {
                                Long userId = Long.parseLong(userIdObj.toString());
                                Usuario usuario = usuarioController.getUsuarioById(userId);
                                if (usuario != null) {
                                    usuario.setPasswordHash(null); // No enviar hash de contraseña
                                    JsonResponse.success(response, usuario);
                                } else {
                                    JsonResponse.notFound(response, "Usuario no encontrado");
                                }
                            } catch (NumberFormatException e) {
                                JsonResponse.badRequest(response, "ID de usuario no válido en token");
                            }
                        } else {
                            JsonResponse.unauthorized(response, "Token de autenticación requerido");
                        }
                        return;
                    }
                    
                    try {
                        Long id = Long.parseLong(segment);
                        // GET /api/usuarios/{id} - Obtener usuario por ID
                        Usuario usuario = usuarioController.getUsuarioById(id);
                        if (usuario != null) {
                            JsonResponse.success(response, usuario);
                        } else {
                            JsonResponse.notFound(response, "Usuario no encontrado");
                        }
                    } catch (NumberFormatException e) {
                        if ("email".equals(segment)) {
                            // GET /api/usuarios/email?email=xxx - Obtener usuario por email
                            String email = request.getParameter("email");
                            if (email != null && !email.trim().isEmpty()) {
                                Usuario usuario = usuarioController.getUsuarioByEmail(email);
                                if (usuario != null) {
                                    JsonResponse.success(response, usuario);
                                } else {
                                    JsonResponse.notFound(response, "Usuario no encontrado");
                                }
                            } else {
                                JsonResponse.badRequest(response, "Parámetro email requerido");
                            }
                        } else if ("role".equals(pathParts[1])) {
                            // GET /api/usuarios/role?rol_id=xxx - Obtener usuarios por rol
                            String rolIdParam = request.getParameter("rol_id");
                            if (rolIdParam != null) {
                                try {
                                    Integer rolId = Integer.parseInt(rolIdParam);
                                    List<Usuario> usuarios = usuarioController.getUsuariosByRole(rolId);
                                    JsonResponse.success(response, usuarios);
                                } catch (NumberFormatException ex) {
                                    JsonResponse.badRequest(response, "ID de rol no válido");
                                }
                            } else {
                                JsonResponse.badRequest(response, "Parámetro rol_id requerido");
                            }
                        } else {
                            JsonResponse.badRequest(response, "ID de usuario no válido");
                        }
                    }
                } else {
                    JsonResponse.badRequest(response, "Ruta no válida");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponse.internalError(response, "Error interno del servidor: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        setCORSHeaders(response);
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // POST /api/usuarios - Crear nuevo usuario
                // Solo ADMIN, RRHH, DIRECTOR pueden crear usuarios
                if (!utils.AuthorizationHelper.checkRoles(request, response, 
                        security.RolePermissions.USUARIOS_WRITE)) {
                    return;
                }
                
                Usuario usuario = parseUsuarioFromRequest(request);
                Usuario nuevoUsuario = usuarioController.createUsuario(usuario);
                
                // 📝 Registrar en auditoría
                AuditService.logCreate(request, AuditLog.ENTIDAD_USUARIO, nuevoUsuario.getId(),
                    String.format("Usuario '%s' creado - Email: %s, Rol ID: %d", 
                        nuevoUsuario.getNombre() + " " + nuevoUsuario.getApellido(),
                        nuevoUsuario.getEmail(), nuevoUsuario.getRolId()));
                
                JsonResponse.created(response, nuevoUsuario);
                
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2 && "auth".equals(pathParts[1])) {
                    // POST /api/usuarios/auth - Autenticar usuario (login)
                    Map<String, String> loginData = parseSimpleJsonFromRequest(request);
                    String email = loginData.get("email");
                    String password = loginData.get("password");
                    
                    Usuario usuario = usuarioController.authenticateUser(email, password);
                    if (usuario != null) {
                        // Generar token JWT
                        security.JwtTokenProvider tokenProvider = new security.JwtTokenProvider();
                        String token = tokenProvider.generateToken(usuario);
                        
                        // � Generar Refresh Token
                        String ipAddress = AuditService.getClientIP(request);
                        String userAgent = request.getHeader("User-Agent");
                        RefreshToken refreshToken = RefreshTokenService.generateRefreshToken(
                            usuario.getId(), ipAddress, userAgent);
                        
                        // 📝 Registrar login exitoso en auditoría
                        AuditService.logLogin(usuario.getId(), usuario.getEmail(), ipAddress, userAgent);
                        
                        // Remover la contraseña de la respuesta
                        usuario.setPasswordHash(null);
                        
                        // Crear respuesta con token y datos del usuario
                        Map<String, Object> authResponse = new HashMap<>();
                        authResponse.put("token", token);
                        authResponse.put("refreshToken", refreshToken != null ? refreshToken.getToken() : null);
                        authResponse.put("usuario", usuario);
                        authResponse.put("expiresIn", 86400); // 24 horas en segundos
                        authResponse.put("refreshExpiresIn", 604800); // 7 días en segundos
                        
                        JsonResponse.success(response, "Autenticación exitosa", authResponse);
                    } else {
                        // 📝 Registrar intento fallido en auditoría
                        String ipAddress = AuditService.getClientIP(request);
                        String userAgent = request.getHeader("User-Agent");
                        AuditService.logLoginFailed(email, "Credenciales inválidas", ipAddress, userAgent);
                        
                        JsonResponse.unauthorized(response, "Credenciales inválidas");
                    }
                    
                } else if (pathParts.length == 2 && "google-auth".equals(pathParts[1])) {
                    // POST /api/usuarios/google-auth - Autenticar/Registrar usuario con Google
                    Map<String, String> googleData = parseSimpleJsonFromRequest(request);
                    String email = googleData.get("email");
                    String displayName = googleData.get("nombre");
                    String uid = googleData.get("uid");
                    String photoURL = googleData.get("photoURL");
                    
                    System.out.println("🔍 Google Auth - Email: " + email);
                    
                    // Buscar si el usuario ya existe
                    Usuario usuario = usuarioController.getUsuarioByEmail(email);
                    
                    if (usuario == null) {
                        // Separar nombre y apellido del displayName
                        String nombre;
                        String apellido;
                        
                        if (displayName != null && displayName.contains(" ")) {
                            String[] nombreParts = displayName.split(" ", 2);
                            nombre = nombreParts[0];
                            apellido = nombreParts[1];
                        } else {
                            // Si no hay espacio, usar todo como nombre y extraer apellido del email
                            nombre = displayName != null ? displayName : email.split("@")[0];
                            apellido = "Google User";
                        }
                        
                        // Crear nuevo usuario con rol USUARIO (id = 3)
                        usuario = new Usuario();
                        usuario.setNombre(nombre);
                        usuario.setApellido(apellido);
                        usuario.setEmail(email);
                        usuario.setPasswordHash(uid); // Usar UID de Google como password (no se usará)
                        usuario.setRolId(3); // Rol USUARIO/CAJERO
                        usuario.setActivo(true);
                        
                        // Guardar en base de datos
                        usuario = usuarioController.createUsuario(usuario);
                        
                        System.out.println("✅ Nuevo usuario creado desde Google: " + email);
                        
                        // 📝 Registrar en auditoría
                        String ipAddress = AuditService.getClientIP(request);
                        String userAgent = request.getHeader("User-Agent");
                        AuditService.logCreate(request, AuditLog.ENTIDAD_USUARIO, usuario.getId(),
                            String.format("Usuario creado desde Google Auth - Email: %s", email));
                    } else {
                        System.out.println("✅ Usuario existente desde Google: " + email);
                    }
                    
                    // Generar token JWT
                    security.JwtTokenProvider tokenProvider = new security.JwtTokenProvider();
                    String token = tokenProvider.generateToken(usuario);
                    
                    // 🔄 Generar Refresh Token
                    String ipAddress = AuditService.getClientIP(request);
                    String userAgent = request.getHeader("User-Agent");
                    RefreshToken refreshToken = RefreshTokenService.generateRefreshToken(
                        usuario.getId(), ipAddress, userAgent);
                    
                    // 📝 Registrar login exitoso en auditoría
                    AuditService.logLogin(usuario.getId(), usuario.getEmail(), ipAddress, userAgent);
                    
                    // Remover la contraseña de la respuesta
                    usuario.setPasswordHash(null);
                    
                    // Crear respuesta con token y datos del usuario
                    Map<String, Object> authResponse = new HashMap<>();
                    authResponse.put("token", token);
                    authResponse.put("refreshToken", refreshToken != null ? refreshToken.getToken() : null);
                    authResponse.put("usuario", usuario);
                    authResponse.put("expiresIn", 86400); // 24 horas en segundos
                    authResponse.put("refreshExpiresIn", 604800); // 7 días en segundos
                    
                    JsonResponse.success(response, "Autenticación con Google exitosa", authResponse);
                    
                } else if (pathParts.length == 2 && "refresh".equals(pathParts[1])) {
                    // POST /api/usuarios/refresh - Refrescar token JWT usando refresh token
                    Map<String, String> refreshData = parseSimpleJsonFromRequest(request);
                    String refreshTokenStr = refreshData.get("refreshToken");
                    
                    if (refreshTokenStr == null || refreshTokenStr.trim().isEmpty()) {
                        JsonResponse.badRequest(response, "Refresh token es requerido");
                        return;
                    }
                    
                    // Validar refresh token
                    RefreshToken refreshToken = RefreshTokenService.validateRefreshToken(refreshTokenStr);
                    
                    if (refreshToken != null) {
                        // Obtener usuario asociado al token
                        Usuario usuario = usuarioController.getUsuarioById(refreshToken.getUsuarioId());
                        
                        if (usuario != null && usuario.getActivo()) {
                            // Generar nuevo JWT
                            security.JwtTokenProvider tokenProvider = new security.JwtTokenProvider();
                            String newToken = tokenProvider.generateToken(usuario);
                            
                            // Rotación de refresh token: revocar el anterior y generar uno nuevo
                            String ipAddress = AuditService.getClientIP(request);
                            String userAgent = request.getHeader("User-Agent");
                            RefreshToken newRefreshToken = RefreshTokenService.rotateToken(
                                refreshTokenStr, usuario.getId(), ipAddress, userAgent);
                            
                            if (newRefreshToken != null) {
                                // Registrar en auditoría
                                AuditService.log(usuario.getId(), usuario.getEmail(), "REFRESH_TOKEN", 
                                    "USUARIO", usuario.getId(), "Token JWT refrescado exitosamente",
                                    ipAddress, userAgent);
                                
                                // Remover contraseña de la respuesta
                                usuario.setPasswordHash(null);
                                
                                // Respuesta con nuevos tokens
                                Map<String, Object> refreshResponse = new HashMap<>();
                                refreshResponse.put("token", newToken);
                                refreshResponse.put("refreshToken", newRefreshToken.getToken());
                                refreshResponse.put("usuario", usuario);
                                refreshResponse.put("expiresIn", 86400); // 24 horas
                                refreshResponse.put("refreshExpiresIn", 604800); // 7 días
                                
                                JsonResponse.success(response, "Token refrescado exitosamente", refreshResponse);
                            } else {
                                JsonResponse.internalError(response, "Error al generar nuevo refresh token");
                            }
                        } else {
                            JsonResponse.unauthorized(response, "Usuario no válido o inactivo");
                        }
                    } else {
                        JsonResponse.unauthorized(response, "Refresh token inválido o expirado");
                    }
                    
                } else {
                    JsonResponse.badRequest(response, "Ruta no válida");
                }
            }
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponse.internalError(response, "Error interno del servidor: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        setCORSHeaders(response);
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo != null) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    try {
                        Long id = Long.parseLong(pathParts[1]);
                        // PUT /api/usuarios/{id} - Actualizar usuario
                        // Solo ADMIN, RRHH, DIRECTOR pueden actualizar usuarios
                        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                                security.RolePermissions.USUARIOS_WRITE)) {
                            return;
                        }
                        
                        Usuario usuario = parseUsuarioFromRequest(request);
                        Usuario usuarioActualizado = usuarioController.updateUsuario(id, usuario);
                        JsonResponse.success(response, "Usuario actualizado exitosamente", usuarioActualizado);
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de usuario no válido");
                    }
                } else if (pathParts.length == 3) {
                    try {
                        Long id = Long.parseLong(pathParts[1]);
                        String action = pathParts[2];
                        
                        if ("password".equals(action)) {
                            // PUT /api/usuarios/{id}/password - Actualizar contraseña
                            Map<String, String> passwordData = parseSimpleJsonFromRequest(request);
                            String newPassword = passwordData.get("password");
                            
                            boolean updated = usuarioController.updatePassword(id, newPassword);
                            if (updated) {
                                JsonResponse.success(response, "Contraseña actualizada exitosamente", null);
                            } else {
                                JsonResponse.internalError(response, "Error al actualizar contraseña");
                            }
                        } else if ("toggle".equals(action)) {
                            // PUT /api/usuarios/{id}/toggle - Activar/desactivar usuario
                            Usuario usuarioActualizado = usuarioController.toggleUsuarioStatus(id);
                            JsonResponse.success(response, "Estado del usuario actualizado", usuarioActualizado);
                        } else {
                            JsonResponse.badRequest(response, "Acción no válida");
                        }
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de usuario no válido");
                    }
                } else {
                    JsonResponse.badRequest(response, "Ruta no válida");
                }
            } else {
                JsonResponse.badRequest(response, "ID de usuario requerido");
            }
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponse.internalError(response, "Error interno del servidor: " + e.getMessage());
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        setCORSHeaders(response);
        
        // Verificar autorización - Solo ADMIN y DIRECTOR pueden eliminar usuarios
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.USUARIOS_DELETE)) {
            return;
        }
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo != null) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    try {
                        Long id = Long.parseLong(pathParts[1]);
                        // DELETE /api/usuarios/{id} - Desactivar usuario
                        boolean deleted = usuarioController.deleteUsuario(id);
                        if (deleted) {
                            JsonResponse.success(response, "Usuario desactivado exitosamente", null);
                        } else {
                            JsonResponse.internalError(response, "Error al desactivar usuario");
                        }
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de usuario no válido");
                    }
                } else {
                    JsonResponse.badRequest(response, "Ruta no válida");
                }
            } else {
                JsonResponse.badRequest(response, "ID de usuario requerido");
            }
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponse.internalError(response, "Error interno del servidor: " + e.getMessage());
        }
    }
    
    /**
     * Parsear Usuario desde el request JSON
     */
    private Usuario parseUsuarioFromRequest(HttpServletRequest request) throws IOException {
        Map<String, String> jsonData = parseSimpleJsonFromRequest(request);
        
        Usuario usuario = new Usuario();
        
        if (jsonData.containsKey("email")) {
            usuario.setEmail(jsonData.get("email"));
        }
        
        if (jsonData.containsKey("password")) {
            usuario.setPasswordHash(jsonData.get("password"));
        }
        
        if (jsonData.containsKey("nombre")) {
            usuario.setNombre(jsonData.get("nombre"));
        }
        
        if (jsonData.containsKey("apellido")) {
            usuario.setApellido(jsonData.get("apellido"));
        }
        
        if (jsonData.containsKey("rol_id")) {
            usuario.setRolId(Integer.parseInt(jsonData.get("rol_id")));
        }
        
        if (jsonData.containsKey("activo")) {
            usuario.setActivo(Boolean.parseBoolean(jsonData.get("activo")));
        }
        
        return usuario;
    }
    
    /**
     * Parsear JSON simple desde el request
     */
    private Map<String, String> parseSimpleJsonFromRequest(HttpServletRequest request) throws IOException {
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        }
        
        // JSON parsing simple para campos básicos
        Map<String, String> result = new HashMap<>();
        String jsonString = json.toString().trim();
        
        if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
            jsonString = jsonString.substring(1, jsonString.length() - 1);
            String[] pairs = jsonString.split(",");
            
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("\"", "");
                    result.put(key, value);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Configurar headers CORS
     */
    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}
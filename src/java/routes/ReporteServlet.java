package routes;

import java.io.IOException;
import java.util.Map;

import controller.ReporteController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JsonResponse;

@WebServlet("/api/reportes/*")
public class ReporteServlet extends HttpServlet {
    
    private ReporteController reporteController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.reporteController = new ReporteController();
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
                // GET /api/reportes - Dashboard general
                Map<String, Object> dashboard = reporteController.getDashboard();
                JsonResponse.success(response, dashboard);
                
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length > 1) {
                    String tipo = pathParts[1];
                    
                    switch (tipo) {
                        case "dashboard":
                            // GET /api/reportes/dashboard - Dashboard general (alias)
                            Map<String, Object> dashboard = reporteController.getDashboard();
                            JsonResponse.success(response, dashboard);
                            break;
                            
                        case "ventas":
                            handleReporteVentas(request, response, pathParts);
                            break;
                            
                        case "compras":
                            handleReporteCompras(request, response);
                            break;
                            
                        case "productos":
                            handleReporteProductos(request, response, pathParts);
                            break;
                            
                        case "inventario":
                            handleReporteInventario(request, response, pathParts);
                            break;
                            
                        case "clientes":
                            handleReporteClientes(request, response);
                            break;
                            
                        default:
                            JsonResponse.error(response, 400, "Tipo de reporte no válido");
                    }
                } else {
                    JsonResponse.error(response, 400, "Ruta no válida");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponse.error(response, 500, "Error interno del servidor: " + e.getMessage());
        }
    }
    
    private void handleReporteVentas(HttpServletRequest request, HttpServletResponse response, String[] pathParts) 
            throws Exception {
        
        String fechaInicio = request.getParameter("fecha_inicio");
        String fechaFin = request.getParameter("fecha_fin");
        String clienteIdParam = request.getParameter("cliente_id");
        Long clienteId = clienteIdParam != null ? Long.parseLong(clienteIdParam) : null;
        
        Map<String, Object> reporteVentas = reporteController.getReporteVentas(fechaInicio, fechaFin, clienteId);
        JsonResponse.success(response, reporteVentas);
    }
    
    private void handleReporteCompras(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String proveedorIdParam = request.getParameter("proveedor_id");
        if (proveedorIdParam != null) {
            Long proveedorId = Long.parseLong(proveedorIdParam);
            Map<String, Object> reporteCompras = reporteController.getComprasPorProveedor(proveedorId);
            JsonResponse.success(response, reporteCompras);
        } else {
            JsonResponse.error(response, 400, "Se requiere proveedor_id para el reporte de compras");
        }
    }
    
    private void handleReporteProductos(HttpServletRequest request, HttpServletResponse response, String[] pathParts) 
            throws Exception {
        
        if (pathParts.length > 2 && "mas-vendidos".equals(pathParts[2])) {
            // GET /api/reportes/productos/mas-vendidos
            Map<String, Object> productosMasVendidos = reporteController.getProductosMasVendidos();
            JsonResponse.success(response, productosMasVendidos);
        } else if (pathParts.length == 2) {
            // GET /api/reportes/productos (alias para mas-vendidos)
            Map<String, Object> productosMasVendidos = reporteController.getProductosMasVendidos();
            JsonResponse.success(response, productosMasVendidos);
        } else {
            JsonResponse.error(response, 400, "Endpoint de productos no válido");
        }
    }
    
    private void handleReporteInventario(HttpServletRequest request, HttpServletResponse response, String[] pathParts) 
            throws Exception {
        
        if (pathParts.length == 2) {
            // GET /api/reportes/inventario
            Map<String, Object> reporteInventario = reporteController.getInventario();
            JsonResponse.success(response, reporteInventario);
        } else if (pathParts.length == 3 && "bajo".equals(pathParts[2])) {
            // GET /api/reportes/inventario/bajo
            String limiteParam = request.getParameter("limite");
            Integer limite = limiteParam != null ? Integer.parseInt(limiteParam) : 10;
            Map<String, Object> inventarioBajo = reporteController.getProductosStockBajo(limite);
            JsonResponse.success(response, inventarioBajo);
        } else {
            JsonResponse.error(response, 400, "Endpoint de inventario no válido");
        }
    }
    
    private void handleReporteClientes(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        Map<String, Object> reporteClientes = reporteController.getClientesFrecuentes();
        JsonResponse.success(response, reporteClientes);
    }
    
    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}

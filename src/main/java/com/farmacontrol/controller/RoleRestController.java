package com.farmacontrol.controller;

import com.farmacontrol.model.Role;
import com.farmacontrol.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * REST Controller moderno para Roles
 * Complementa tus servlets existentes con endpoints REST
 */
@RestController
@RequestMapping("/rest/roles")
@CrossOrigin(origins = "*")
public class RoleRestController {
    
    private final RoleService roleService;
    
    public RoleRestController() {
        this.roleService = new RoleService();
    }
    
    /**
     * GET /rest/roles - Obtener todos los roles
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        try {
            List<Role> roles = roleService.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", roles);
            response.put("message", "Roles obtenidos exitosamente");
            response.put("count", roles.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Error al obtener roles: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GET /rest/roles/{id} - Obtener rol por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoleById(@PathVariable("id") int id) {
        try {
            Role role = roleService.findById(id);
            
            Map<String, Object> response = new HashMap<>();
            
            if (role != null) {
                response.put("success", true);
                response.put("data", role);
                response.put("message", "Rol encontrado");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Rol no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Error al obtener rol: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * POST /rest/roles - Crear nuevo rol
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(@RequestBody Role role) {
        try {
            Map<String, Object> response = new HashMap<>();
            
            if (role.getNombre() == null || role.getNombre().trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "El nombre del rol es requerido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            Role createdRole = roleService.create(role);
            
            if (createdRole != null) {
                response.put("success", true);
                response.put("data", createdRole);
                response.put("message", "Rol creado exitosamente");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("success", false);
                response.put("error", "Error al crear el rol");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Error al crear rol: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
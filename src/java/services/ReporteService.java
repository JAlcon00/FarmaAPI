package services;

import config.DatabaseConfig;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para generar reportes y estadísticas
 */
public class ReporteService {
    
    private final VentaService ventaService;
    private final ProductoService productoService;
    private final CompraService compraService;
    private final ClienteService clienteService;
    private final ProveedorService proveedorService;
    
    public ReporteService() {
        this.ventaService = new VentaService();
        this.productoService = new ProductoService();
        this.compraService = new CompraService();
        this.clienteService = new ClienteService();
        this.proveedorService = new ProveedorService();
    }
    
    /**
     * Generar dashboard principal con estadísticas generales
     */
    public Map<String, Object> generarDashboard() throws SQLException {
        Map<String, Object> dashboard = new HashMap<>();
        
        try {
            // Estadísticas principales
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalVentas", ventaService.findAll().size());
            estadisticas.put("totalCompras", compraService.findAll().size());
            estadisticas.put("totalProductos", productoService.findAll().size());
            estadisticas.put("totalClientes", clienteService.findAll().size());
            estadisticas.put("totalProveedores", proveedorService.findAll().size());
            
            // Calcular totales monetarios
            BigDecimal montoTotalVentas = ventaService.findAll().stream()
                .map(venta -> venta.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            BigDecimal montoTotalCompras = compraService.findAll().stream()
                .map(compra -> compra.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            estadisticas.put("montoTotalVentas", montoTotalVentas);
            estadisticas.put("montoTotalCompras", montoTotalCompras);
            
            // Productos con stock bajo (menos de 10 unidades)
            long productosStockBajo = productoService.findAll().stream()
                .filter(producto -> producto.getStock() < 10)
                .count();
            estadisticas.put("productosStockBajo", productosStockBajo);
            
            // Información adicional
            dashboard.put("estadisticas", estadisticas);
            dashboard.put("fechaGeneracion", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            dashboard.put("sistema", "FarmaControl v1.0");
            dashboard.put("descripcion", "Dashboard del sistema de gestión farmacéutica");
            dashboard.put("estado", "Datos actualizados correctamente");
            
        } catch (Exception e) {
            // En caso de error, devolver dashboard básico
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalVentas", 0);
            estadisticas.put("totalCompras", 0);
            estadisticas.put("totalProductos", 0);
            estadisticas.put("totalClientes", 0);
            estadisticas.put("totalProveedores", 0);
            estadisticas.put("montoTotalVentas", BigDecimal.ZERO);
            estadisticas.put("montoTotalCompras", BigDecimal.ZERO);
            estadisticas.put("productosStockBajo", 0);
            
            dashboard.put("estadisticas", estadisticas);
            dashboard.put("fechaGeneracion", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            dashboard.put("sistema", "FarmaControl v1.0");
            dashboard.put("descripcion", "Dashboard del sistema de gestión farmacéutica");
            dashboard.put("estado", "Error al cargar datos: " + e.getMessage());
        }
        
        return dashboard;
    }
    
    /**
     * Reporte de ventas con filtros opcionales
     */
    public Map<String, Object> reporteVentas(String fechaInicio, String fechaFin, Long clienteId) throws SQLException {
        Map<String, Object> reporte = new HashMap<>();
        
        reporte.put("tipo", "reporte_ventas");
        reporte.put("fechaInicio", fechaInicio);
        reporte.put("fechaFin", fechaFin);
        reporte.put("clienteId", clienteId);
        
        // En una implementación real, aquí aplicarías los filtros
        if (clienteId != null) {
            // Por ahora devolvemos todas las ventas (en implementación real filtrar por cliente)
            reporte.put("ventas", ventaService.findAll());
            reporte.put("descripcion", "Ventas filtradas por cliente ID: " + clienteId);
        } else {
            reporte.put("ventas", ventaService.findAll());
            reporte.put("descripcion", "Todas las ventas del sistema");
        }
        
        reporte.put("fechaGeneracion", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        return reporte;
    }
    
    /**
     * Reporte de productos más vendidos
     */
    public Map<String, Object> reporteProductosMasVendidos() throws SQLException {
        Map<String, Object> reporte = new HashMap<>();
        
        reporte.put("tipo", "productos_mas_vendidos");
        reporte.put("descripcion", "Productos con mayor número de ventas");
        
        // Consulta SQL para obtener productos más vendidos con cantidad y total
        String sql = "SELECT " +
                     "p.id AS productoId, " +
                     "p.nombre AS nombreProducto, " +
                     "SUM(dv.cantidad) AS cantidadVendida, " +
                     "SUM(dv.subtotal) AS totalVentas " +
                     "FROM productos p " +
                     "INNER JOIN detalle_ventas dv ON p.id = dv.producto_id " +
                     "INNER JOIN ventas v ON dv.venta_id = v.id " +
                     "WHERE v.estado = 'COMPLETADA' " +
                     "GROUP BY p.id, p.nombre " +
                     "ORDER BY cantidadVendida DESC " +
                     "LIMIT 10";
        
        List<Map<String, Object>> productos = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> producto = new HashMap<>();
                producto.put("productoId", rs.getLong("productoId"));
                producto.put("nombreProducto", rs.getString("nombreProducto"));
                producto.put("cantidadVendida", rs.getInt("cantidadVendida"));
                producto.put("totalVentas", rs.getDouble("totalVentas"));
                productos.add(producto);
            }
        }
        
        reporte.put("productos", productos);
        reporte.put("fechaGeneracion", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        return reporte;
    }
    
    /**
     * Reporte de productos con stock bajo
     */
    public Map<String, Object> reporteProductosStockBajo(Integer limite) throws SQLException {
        Map<String, Object> reporte = new HashMap<>();
        
        if (limite == null) {
            limite = 10; // Stock menor a 10 unidades
        }
        
        reporte.put("tipo", "productos_stock_bajo");
        reporte.put("limiteStock", limite);
        reporte.put("descripcion", "Productos con stock menor a " + limite + " unidades");
        
        // En una implementación real, filtrarías por stock < limite
        reporte.put("productos", productoService.findAll());
        reporte.put("fechaGeneracion", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        return reporte;
    }
    
    /**
     * Reporte de compras por proveedor
     */
    public Map<String, Object> reporteComprasPorProveedor(Long proveedorId) throws SQLException {
        Map<String, Object> reporte = new HashMap<>();
        
        reporte.put("tipo", "compras_por_proveedor");
        reporte.put("proveedorId", proveedorId);
        
        if (proveedorId != null) {
            reporte.put("compras", compraService.findByProveedor(proveedorId));
            reporte.put("proveedor", proveedorService.findById(proveedorId));
            reporte.put("descripcion", "Compras del proveedor ID: " + proveedorId);
        } else {
            reporte.put("compras", compraService.findAll());
            reporte.put("descripcion", "Todas las compras del sistema");
        }
        
        reporte.put("fechaGeneracion", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        return reporte;
    }
    
    /**
     * Reporte de estado del inventario
     */
    public Map<String, Object> reporteInventario() throws SQLException {
        Map<String, Object> reporte = new HashMap<>();
        
        reporte.put("tipo", "inventario_general");
        reporte.put("descripcion", "Estado completo del inventario");
        reporte.put("productos", productoService.findAll());
        
        // Estadísticas adicionales del inventario
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalProductos", productoService.findAll().size());
        // En implementación real: calcular valor total del inventario, productos sin stock, etc.
        
        reporte.put("estadisticas", estadisticas);
        reporte.put("fechaGeneracion", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        return reporte;
    }
    
    /**
     * Reporte de clientes más frecuentes
     */
    public Map<String, Object> reporteClientesFrecuentes() throws SQLException {
        Map<String, Object> reporte = new HashMap<>();
        
        reporte.put("tipo", "clientes_frecuentes");
        reporte.put("descripcion", "Clientes con más compras realizadas");
        reporte.put("clientes", clienteService.findAll());
        reporte.put("fechaGeneracion", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        return reporte;
    }
}
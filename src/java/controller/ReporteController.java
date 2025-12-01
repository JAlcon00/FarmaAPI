package controller;

import java.sql.SQLException;
import java.util.Map;

import services.ReporteService;

/**
 * Controlador para reportes y estadísticas
 */
public class ReporteController {
    private final ReporteService reporteService;
    
    public ReporteController() {
        this.reporteService = new ReporteService();
    }
    
    /**
     * Obtener dashboard principal
     */
    public Map<String, Object> getDashboard() throws SQLException {
        return reporteService.generarDashboard();
    }
    
    /**
     * Reporte de ventas
     */
    public Map<String, Object> getReporteVentas(String fechaInicio, String fechaFin, Long clienteId) throws SQLException {
        return reporteService.reporteVentas(fechaInicio, fechaFin, clienteId);
    }
    
    /**
     * Reporte de productos más vendidos
     */
    public Map<String, Object> getProductosMasVendidos() throws SQLException {
        return reporteService.reporteProductosMasVendidos();
    }
    
    /**
     * Reporte de productos con stock bajo
     */
    public Map<String, Object> getProductosStockBajo(Integer limite) throws SQLException {
        return reporteService.reporteProductosStockBajo(limite);
    }
    
    /**
     * Reporte de compras por proveedor
     */
    public Map<String, Object> getComprasPorProveedor(Long proveedorId) throws SQLException {
        return reporteService.reporteComprasPorProveedor(proveedorId);
    }
    
    /**
     * Reporte de inventario
     */
    public Map<String, Object> getInventario() throws SQLException {
        return reporteService.reporteInventario();
    }
    
    /**
     * Reporte de clientes frecuentes
     */
    public Map<String, Object> getClientesFrecuentes() throws SQLException {
        return reporteService.reporteClientesFrecuentes();
    }
}
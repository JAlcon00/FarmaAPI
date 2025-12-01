package controller;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import model.DetalleVenta;
import model.Producto;
import model.Venta;
import services.ClienteService;
import services.ProductoService;
import services.VentaService;

/**
 * Controlador para ventas
 */
public class VentaController {
    private final VentaService ventaService;
    private final ProductoService productoService;
    private final ClienteService clienteService;
    
    public VentaController() {
        this.ventaService = new VentaService();
        this.productoService = new ProductoService();
        this.clienteService = new ClienteService();
    }
    
    /**
     * Obtener todas las ventas
     */
    public List<Venta> getAllVentas() throws SQLException {
        return ventaService.findAll();
    }
    
    /**
     * Obtener venta por ID
     */
    public Venta getVentaById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de venta inválido");
        }
        
        Venta venta = ventaService.findById(id);
        if (venta == null) {
            throw new SQLException("Venta no encontrada con ID: " + id);
        }
        
        return venta;
    }
    
    /**
     * Obtener ventas por rango de fechas
     */
    public List<Venta> getVentasByFecha(Date fechaInicio, Date fechaFin) throws SQLException {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas son requeridas");
        }
        
        if (fechaInicio.after(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha fin");
        }
        
        return ventaService.findByFecha(fechaInicio, fechaFin);
    }
    
    /**
     * Obtener detalles de una venta
     */
    public List<DetalleVenta> getDetallesVenta(Long ventaId) throws SQLException {
        if (ventaId == null || ventaId <= 0) {
            throw new IllegalArgumentException("ID de venta inválido");
        }
        
        // Verificar que la venta existe
        if (ventaService.findById(ventaId) == null) {
            throw new SQLException("Venta no encontrada con ID: " + ventaId);
        }
        
        return ventaService.getDetalles(ventaId);
    }
    
    /**
     * Crear una nueva venta con sus detalles
     */
    public Venta createVenta(Venta venta, List<DetalleVenta> detalles) throws SQLException {
        // Validaciones generales
        if (venta.getUsuarioId() == null || venta.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("El usuario es requerido");
        }
        
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un producto");
        }
        
        // Validar cliente si se proporciona
        if (venta.getClienteId() != null && venta.getClienteId() > 0) {
            if (clienteService.findById(venta.getClienteId()) == null) {
                throw new SQLException("Cliente no encontrado con ID: " + venta.getClienteId());
            }
        }
        
        // Validar método de pago
        String[] metodosValidos = {"EFECTIVO", "TARJETA", "TRANSFERENCIA"};
        boolean metodoValido = false;
        for (String metodo : metodosValidos) {
            if (metodo.equals(venta.getMetodoPago())) {
                metodoValido = true;
                break;
            }
        }
        if (!metodoValido) {
            throw new IllegalArgumentException("Método de pago inválido. Use: EFECTIVO, TARJETA o TRANSFERENCIA");
        }
        
        // Validar cada detalle y verificar stock
        BigDecimal subtotalCalculado = BigDecimal.ZERO;
        
        for (DetalleVenta detalle : detalles) {
            // Validar producto
            if (detalle.getProductoId() == null || detalle.getProductoId() <= 0) {
                throw new IllegalArgumentException("ID de producto inválido en detalle");
            }
            
            Producto producto = productoService.findById(detalle.getProductoId());
            if (producto == null) {
                throw new SQLException("Producto no encontrado con ID: " + detalle.getProductoId());
            }
            
            // Verificar stock disponible
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            
            if (producto.getStock() < detalle.getCantidad()) {
                throw new IllegalArgumentException(
                    "Stock insuficiente para el producto: " + producto.getNombre() + 
                    " (Disponible: " + producto.getStock() + ", Solicitado: " + detalle.getCantidad() + ")"
                );
            }
            
            // Validar precio
            if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
            }
            
            // Calcular subtotal del detalle
            BigDecimal subtotalDetalle = detalle.getPrecioUnitario()
                .multiply(new BigDecimal(detalle.getCantidad()));
            detalle.setSubtotal(subtotalDetalle);
            
            // Asegurar que tiene el nombre del producto
            if (detalle.getNombreProducto() == null || detalle.getNombreProducto().trim().isEmpty()) {
                detalle.setNombreProducto(producto.getNombre());
            }
            
            subtotalCalculado = subtotalCalculado.add(subtotalDetalle);
        }
        
        // Establecer valores calculados
        venta.setSubtotal(subtotalCalculado);
        
        // Aplicar descuento si existe
        BigDecimal descuento = venta.getDescuento() != null ? venta.getDescuento() : BigDecimal.ZERO;
        if (descuento.compareTo(BigDecimal.ZERO) < 0 || descuento.compareTo(subtotalCalculado) > 0) {
            throw new IllegalArgumentException("El descuento es inválido");
        }
        
        // Calcular impuestos (16% IVA en México)
        BigDecimal baseImponible = subtotalCalculado.subtract(descuento);
        BigDecimal impuestos = baseImponible.multiply(new BigDecimal("0.16"));
        venta.setImpuestos(impuestos);
        
        // Calcular total
        BigDecimal total = baseImponible.add(impuestos);
        venta.setTotal(total);
        
        // Crear la venta con sus detalles (transacción)
        return ventaService.createConDetalles(venta, detalles);
    }
    
    /**
     * Cancelar una venta
     */
    public boolean cancelarVenta(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de venta inválido");
        }
        
        // Verificar que existe
        Venta existing = ventaService.findById(id);
        if (existing == null) {
            throw new SQLException("Venta no encontrada con ID: " + id);
        }
        
        // Verificar que no esté ya cancelada
        if ("CANCELADA".equals(existing.getEstado())) {
            throw new IllegalArgumentException("La venta ya está cancelada");
        }
        
        return ventaService.cancelar(id);
    }
    
    /**
     * Obtener total de ventas por período
     */
    public BigDecimal getTotalVentasPorPeriodo(Date fechaInicio, Date fechaFin) throws SQLException {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas son requeridas");
        }
        
        if (fechaInicio.after(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha fin");
        }
        
        return ventaService.getTotalVentasPorPeriodo(fechaInicio, fechaFin);
    }
}

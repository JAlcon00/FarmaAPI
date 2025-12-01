package controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import model.Compra;
import model.DetalleCompra;
import model.Producto;
import services.CompraService;
import services.ProductoService;
import services.ProveedorService;

/**
 * Controlador para compras
 */
public class CompraController {
    private final CompraService compraService;
    private final ProductoService productoService;
    private final ProveedorService proveedorService;
    
    public CompraController() {
        this.compraService = new CompraService();
        this.productoService = new ProductoService();
        this.proveedorService = new ProveedorService();
    }
    
    /**
     * Obtener todas las compras
     */
    public List<Compra> getAllCompras() throws SQLException {
        return compraService.findAll();
    }
    
    /**
     * Obtener compra por ID
     */
    public Compra getCompraById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de compra inválido");
        }
        
        Compra compra = compraService.findById(id);
        if (compra == null) {
            throw new SQLException("Compra no encontrada con ID: " + id);
        }
        
        return compra;
    }
    
    /**
     * Obtener compras por proveedor
     */
    public List<Compra> getComprasByProveedor(Long proveedorId) throws SQLException {
        if (proveedorId == null || proveedorId <= 0) {
            throw new IllegalArgumentException("ID de proveedor inválido");
        }
        
        // Verificar que el proveedor existe
        if (proveedorService.findById(proveedorId) == null) {
            throw new SQLException("Proveedor no encontrado con ID: " + proveedorId);
        }
        
        return compraService.findByProveedor(proveedorId);
    }
    
    /**
     * Obtener detalles de una compra
     */
    public List<DetalleCompra> getDetallesCompra(Long compraId) throws SQLException {
        if (compraId == null || compraId <= 0) {
            throw new IllegalArgumentException("ID de compra inválido");
        }
        
        // Verificar que la compra existe
        if (compraService.findById(compraId) == null) {
            throw new SQLException("Compra no encontrada con ID: " + compraId);
        }
        
        return compraService.getDetalles(compraId);
    }
    
    /**
     * Crear una nueva compra con sus detalles
     */
    public Compra createCompra(Compra compra, List<DetalleCompra> detalles) throws SQLException {
        // Validaciones generales
        if (compra.getProveedorId() == null || compra.getProveedorId() <= 0) {
            throw new IllegalArgumentException("El proveedor es requerido");
        }
        
        if (compra.getUsuarioId() == null || compra.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("El usuario es requerido");
        }
        
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La compra debe tener al menos un producto");
        }
        
        // Validar proveedor
        if (proveedorService.findById(compra.getProveedorId()) == null) {
            throw new SQLException("Proveedor no encontrado con ID: " + compra.getProveedorId());
        }
        
        // Validar estado
        if (compra.getEstado() != null) {
            String[] estadosValidos = {"PENDIENTE", "RECIBIDA", "CANCELADA"};
            boolean estadoValido = false;
            for (String estado : estadosValidos) {
                if (estado.equals(compra.getEstado())) {
                    estadoValido = true;
                    break;
                }
            }
            if (!estadoValido) {
                throw new IllegalArgumentException("Estado inválido. Use: PENDIENTE, RECIBIDA o CANCELADA");
            }
        }
        
        // Validar cada detalle
        BigDecimal subtotalCalculado = BigDecimal.ZERO;
        
        for (DetalleCompra detalle : detalles) {
            // Validar producto
            if (detalle.getProductoId() == null || detalle.getProductoId() <= 0) {
                throw new IllegalArgumentException("ID de producto inválido en detalle");
            }
            
            Producto producto = productoService.findById(detalle.getProductoId());
            if (producto == null) {
                throw new SQLException("Producto no encontrado con ID: " + detalle.getProductoId());
            }
            
            // Validar cantidad
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            
            // Validar precio
            if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
            }
            
            // Calcular subtotal del detalle
            BigDecimal subtotalDetalle = detalle.getPrecioUnitario()
                .multiply(new BigDecimal(detalle.getCantidad()));
            detalle.setSubtotal(subtotalDetalle);
            
            subtotalCalculado = subtotalCalculado.add(subtotalDetalle);
        }
        
        // Establecer valores calculados
        compra.setSubtotal(subtotalCalculado);
        
        // Calcular impuestos (16% IVA en México)
        BigDecimal impuestos = subtotalCalculado.multiply(new BigDecimal("0.16"));
        compra.setImpuestos(impuestos);
        
        // Calcular total
        BigDecimal total = subtotalCalculado.add(impuestos);
        compra.setTotal(total);
        
        // Crear la compra con sus detalles (transacción)
        return compraService.createConDetalles(compra, detalles);
    }
    
    /**
     * Actualizar estado de una compra
     */
    public boolean updateEstadoCompra(Long id, String nuevoEstado) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de compra inválido");
        }
        
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado es requerido");
        }
        
        // Validar estado
        String[] estadosValidos = {"PENDIENTE", "RECIBIDA", "CANCELADA"};
        boolean estadoValido = false;
        for (String estado : estadosValidos) {
            if (estado.equals(nuevoEstado)) {
                estadoValido = true;
                break;
            }
        }
        if (!estadoValido) {
            throw new IllegalArgumentException("Estado inválido. Use: PENDIENTE, RECIBIDA o CANCELADA");
        }
        
        // Verificar que existe
        Compra existing = compraService.findById(id);
        if (existing == null) {
            throw new SQLException("Compra no encontrada con ID: " + id);
        }
        
        return compraService.updateEstado(id, nuevoEstado);
    }
    
    /**
     * Cancelar una compra
     */
    public boolean cancelarCompra(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de compra inválido");
        }
        
        // Verificar que existe
        Compra existing = compraService.findById(id);
        if (existing == null) {
            throw new SQLException("Compra no encontrada con ID: " + id);
        }
        
        // Verificar que no esté ya cancelada
        if ("CANCELADA".equals(existing.getEstado())) {
            throw new IllegalArgumentException("La compra ya está cancelada");
        }
        
        return compraService.cancelar(id);
    }
}

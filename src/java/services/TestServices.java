package services;

import model.Categoria;
import model.Producto;

/**
 * Test rápido de los services
 */
public class TestServices {
    
    public static void main(String[] args) {
        System.out.println("🧪 Probando Services...\n");
        
        try {
            // Test CategoriaService
            System.out.println("📂 Test CategoriaService:");
            CategoriaService categoriaService = new CategoriaService();
            var categorias = categoriaService.findAll();
            System.out.println("   Total de categorías: " + categorias.size());
            if (!categorias.isEmpty()) {
                Categoria primera = categorias.get(0);
                System.out.println("   Primera categoría: " + primera.getNombre());
            }
            
            // Test ProductoService
            System.out.println("\n📦 Test ProductoService:");
            ProductoService productoService = new ProductoService();
            var productos = productoService.findAll();
            System.out.println("   Total de productos: " + productos.size());
            if (!productos.isEmpty()) {
                Producto primero = productos.get(0);
                System.out.println("   Primer producto: " + primero.getNombre());
                System.out.println("   Precio: $" + primero.getPrecio());
                System.out.println("   Stock: " + primero.getStock());
                if (primero.getCategoria() != null) {
                    System.out.println("   Categoría: " + primero.getCategoria().getNombre());
                }
            }
            
            // Test productos con stock bajo
            System.out.println("\n⚠️  Test Productos con Stock Bajo:");
            var productosLowStock = productoService.findLowStock();
            System.out.println("   Productos con stock bajo: " + productosLowStock.size());
            
            // Test ClienteService
            System.out.println("\n👥 Test ClienteService:");
            ClienteService clienteService = new ClienteService();
            var clientes = clienteService.findAll();
            System.out.println("   Total de clientes: " + clientes.size());
            
            // Test ProveedorService
            System.out.println("\n🏢 Test ProveedorService:");
            ProveedorService proveedorService = new ProveedorService();
            var proveedores = proveedorService.findAll();
            System.out.println("   Total de proveedores: " + proveedores.size());
            
            // Test RoleService
            System.out.println("\n🔐 Test RoleService:");
            RoleService roleService = new RoleService();
            var roles = roleService.findAll();
            System.out.println("   Total de roles: " + roles.size());
            
            System.out.println("\n✅ ¡Todos los tests pasaron correctamente!");
            
        } catch (Exception e) {
            System.err.println("\n❌ Error en los tests:");
            e.printStackTrace();
        }
    }
}

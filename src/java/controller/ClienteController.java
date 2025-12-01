package controller;

import dto.ClienteDTO;
import java.sql.SQLException;
import java.util.List;
import model.Cliente;
import services.ClienteService;
import services.ValidationService;

/**
 * Controlador para clientes
 */
public class ClienteController {
    private final ClienteService clienteService;
    
    public ClienteController() {
        this.clienteService = new ClienteService();
    }
    
    /**
     * Obtener todos los clientes
     */
    public List<Cliente> getAllClientes() throws SQLException {
        return clienteService.findAll();
    }
    
    /**
     * Obtener cliente por ID
     */
    public Cliente getClienteById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        
        Cliente cliente = clienteService.findById(id);
        if (cliente == null) {
            throw new SQLException("Cliente no encontrado con ID: " + id);
        }
        
        return cliente;
    }
    
    /**
     * Buscar clientes por nombre
     */
    public List<Cliente> searchClientes(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda es requerido");
        }
        
        return clienteService.findByNombre(nombre);
    }
    
    /**
     * Crear un nuevo cliente usando DTO con validaciones
     */
    public Cliente createCliente(ClienteDTO clienteDTO) throws SQLException {
        // Validar DTO usando Bean Validation
        ValidationService.validate(clienteDTO);
        
        // Convertir DTO a Entity
        Cliente cliente = convertToEntity(clienteDTO);
        
        return clienteService.create(cliente);
    }
    
    /**
     * Crear cliente usando Entity (mantener compatibilidad)
     */
    public Cliente createCliente(Cliente cliente) throws SQLException {
        // Validaciones básicas sin usar DTO
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es requerido");
        }
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono es requerido");
        }
        
        // Crear directamente sin pasar por validaciones del DTO
        return clienteService.create(cliente);
    }
    
    /**
     * Actualizar un cliente usando DTO con validaciones
     */
    public boolean updateCliente(ClienteDTO clienteDTO) throws SQLException {
        // Validar ID
        if (clienteDTO.getId() == null || clienteDTO.getId() <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        
        // Validar DTO usando Bean Validation
        ValidationService.validate(clienteDTO);
        
        // Verificar que existe
        Cliente existing = clienteService.findById(clienteDTO.getId());
        if (existing == null) {
            throw new SQLException("Cliente no encontrado con ID: " + clienteDTO.getId());
        }
        
        // Convertir DTO a Entity
        Cliente cliente = convertToEntity(clienteDTO);
        
        return clienteService.update(cliente);
    }
    
    /**
     * Actualizar cliente usando Entity (mantener compatibilidad)
     */
    public boolean updateCliente(Cliente cliente) throws SQLException {
        // Convertir Entity a DTO para validar
        ClienteDTO dto = convertToDTO(cliente);
        
        // Usar el método principal con DTO
        return updateCliente(dto);
    }
    
    /**
     * Eliminar (desactivar) un cliente
     */
    public boolean deleteCliente(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        
        // Verificar que existe
        Cliente existing = clienteService.findById(id);
        if (existing == null) {
            throw new SQLException("Cliente no encontrado con ID: " + id);
        }
        
        return clienteService.delete(id);
    }
    
    /**
     * Convierte un ClienteDTO a entidad Cliente
     */
    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setId(dto.getId());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        
        // Convertir LocalDate a java.sql.Date si existe
        if (dto.getFechaNacimiento() != null) {
            cliente.setFechaNacimiento(java.sql.Date.valueOf(dto.getFechaNacimiento()));
        }
        
        // Los campos tipo, estado, notas, rfc no existen en la entidad Cliente
        // Solo se usan en el DTO para validaciones
        return cliente;
    }
    
    /**
     * Convierte una entidad Cliente a ClienteDTO
     */
    private ClienteDTO convertToDTO(Cliente entity) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setDireccion(entity.getDireccion());
        
        // Convertir java.sql.Date a LocalDate si existe
        if (entity.getFechaNacimiento() != null) {
            dto.setFechaNacimiento(entity.getFechaNacimiento().toLocalDate());
        }
        
        // Los campos tipo, estado, notas, rfc no existen en la entidad Cliente
        // Mantienen sus valores por defecto del DTO
        return dto;
    }
}

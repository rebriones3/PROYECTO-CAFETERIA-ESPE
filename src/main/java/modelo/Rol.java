package modelo;

import java.util.HashSet;
import java.util.Set;

public class Rol {
    private String nombre;
    private String descripcion;
    private Set<String> permisos;
    private boolean activo;
    
    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = new HashSet<>();
        this.activo = true;
    }
    
    // Permisos disponibles en el sistema
    public static class Permisos {
        // ESTUDIANTE
        public static final String VER_MENU = "ver_menu";
        public static final String HACER_PEDIDO = "hacer_pedido";
        public static final String REALIZAR_PAGO = "realizar_pago";
        
        // PERSONAL
        public static final String VER_PEDIDOS = "ver_pedidos";
        public static final String CAMBIAR_ESTADO_PEDIDO = "cambiar_estado_pedido";
        public static final String MARCAR_ENTREGADO = "marcar_entregado";
        
        // ADMINISTRADOR
        public static final String VER_PRODUCTOS = "ver_productos";
        public static final String AGREGAR_PRODUCTO = "agregar_producto";
        public static final String EDITAR_PRODUCTO = "editar_producto";
        public static final String ELIMINAR_PRODUCTO = "eliminar_producto";
        public static final String GESTIONAR_USUARIOS = "gestionar_usuarios";
        public static final String GENERAR_REPORTES = "generar_reportes";
        public static final String GESTIONAR_ROLES = "gestionar_roles";
        
        // Método para obtener todos los permisos
        public static String[] obtenerTodosLosPermisos() {
            return new String[] {
                VER_MENU, HACER_PEDIDO, REALIZAR_PAGO,
                VER_PEDIDOS, CAMBIAR_ESTADO_PEDIDO, MARCAR_ENTREGADO,
                VER_PRODUCTOS, AGREGAR_PRODUCTO, EDITAR_PRODUCTO, 
                ELIMINAR_PRODUCTO, GESTIONAR_USUARIOS, GENERAR_REPORTES,
                GESTIONAR_ROLES
            };
        }
        
        // Obtener permisos por categoría
        public static String[] obtenerPermisosEstudiante() {
            return new String[] {VER_MENU, HACER_PEDIDO, REALIZAR_PAGO};
        }
        
        public static String[] obtenerPermisosPersonal() {
            return new String[] {VER_PEDIDOS, CAMBIAR_ESTADO_PEDIDO, MARCAR_ENTREGADO};
        }
        
        public static String[] obtenerPermisosAdministrador() {
            return new String[] {
                VER_PRODUCTOS, AGREGAR_PRODUCTO, EDITAR_PRODUCTO, 
                ELIMINAR_PRODUCTO, GESTIONAR_USUARIOS, GENERAR_REPORTES,
                GESTIONAR_ROLES
            };
        }
        
        // Descripción legible de permisos
        public static String obtenerDescripcion(String permiso) {
            switch (permiso) {
                case VER_MENU: return "Ver menú de productos";
                case HACER_PEDIDO: return "Realizar pedidos";
                case REALIZAR_PAGO: return "Procesar pagos";
                case VER_PEDIDOS: return "Ver lista de pedidos";
                case CAMBIAR_ESTADO_PEDIDO: return "Cambiar estado de pedidos";
                case MARCAR_ENTREGADO: return "Marcar pedidos como entregados";
                case VER_PRODUCTOS: return "Ver productos";
                case AGREGAR_PRODUCTO: return "Agregar nuevos productos";
                case EDITAR_PRODUCTO: return "Editar productos existentes";
                case ELIMINAR_PRODUCTO: return "Eliminar productos";
                case GESTIONAR_USUARIOS: return "Gestionar usuarios del sistema";
                case GENERAR_REPORTES: return "Generar reportes de ventas";
                case GESTIONAR_ROLES: return "Crear y gestionar roles";
                default: return permiso;
            }
        }
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Set<String> getPermisos() {
        return permisos;
    }
    
    public void setPermisos(Set<String> permisos) {
        this.permisos = permisos;
    }
    
    public void agregarPermiso(String permiso) {
        this.permisos.add(permiso);
    }
    
    public void removerPermiso(String permiso) {
        this.permisos.remove(permiso);
    }
    
    public boolean tienePermiso(String permiso) {
        return permisos.contains(permiso);
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return "Rol{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", permisos=" + permisos.size() +
                ", activo=" + activo +
                '}';
    }
}

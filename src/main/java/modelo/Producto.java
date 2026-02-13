package modelo;

public class Producto {
    private String nombre;
    private double precio;
    private String categoria;
    private String tamaño;
    private int cantidad;
    private String disponible;
    
    public Producto(String nombre, double precio, String categoria, String tamaño) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.tamaño = tamaño;
        this.cantidad = 1;
        this.disponible = "Si"; 
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTamaño() {
        return tamaño;
    }

    public void setTamaño(String tamaño) {
        this.tamaño = tamaño;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public String getDisponible() {
        return disponible;
    }
    
    public void setDisponible(String disponible) {
        this.disponible = disponible;
    }
    
    public double getSubtotal() {
        return precio * cantidad;
    }
    
    @Override
    public String toString() {
        return "Producto{" +
                "nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", categoria='" + categoria + '\'' +
                ", tamaño='" + tamaño + '\'' +
                ", disponible='" + disponible + '\'' +
                '}';
    }
}
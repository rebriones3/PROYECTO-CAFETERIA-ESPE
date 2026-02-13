package modelo;

public class Pedido {
    private String estudiante;
    private String productos;
    private double total;
    private String estado;
    private String codigoTransaccion;
    
    public Pedido(String estudiante, String productos, double total, String estado, String codigoTransaccion) {
        this.estudiante = estudiante;
        this.productos = productos;
        this.total = total;
        this.estado = estado;
        this.codigoTransaccion = codigoTransaccion;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getProductos() {
        return productos;
    }

    public void setProductos(String productos) {
        this.productos = productos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoTransaccion() {
        return codigoTransaccion;
    }

    public void setCodigoTransaccion(String codigoTransaccion) {
        this.codigoTransaccion = codigoTransaccion;
    }
}
package modelo;

public class Usuario {
    private String rol;
    private String correo;
    private String contraseña;
    
    public Usuario(String rol, String correo, String contraseña) {
        this.rol = rol;
        this.correo = correo;
        this.contraseña = contraseña != null ? contraseña : "";
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña != null ? contraseña : "";
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    @Override
    public String toString() {
        return rol + "," + correo + "," + contraseña;
    }
}
package Clases;

public class Usuario {
    private String uid;
    private String nombre, apellido;
    private String correo;
    private String genero;
    private String rol;
    private String inventarioid;

    public Usuario(String nombre, String apellido, String correo, String genero, String rol, String inventarioid) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.genero = genero;
        this.rol = rol;
        this.inventarioid = inventarioid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getInventarioid() {
        return inventarioid;
    }

    public void setInventarioid(String inventarioid) {
        this.inventarioid = inventarioid;
    }
}

package Clases;

public class Prestatario
{
    private String nombre, apellido, run, carrera, telefono, correo;

    public Prestatario(String nombre, String apellido, String carrera, String telefono, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.carrera = carrera;
        this.telefono = telefono;
        this.correo = correo;
    }


    public Prestatario()
    {
        //constructor vacío
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

    public String getRut() {
        return run;
    }

    public void setRut(String rut) {
        this.run = rut;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}

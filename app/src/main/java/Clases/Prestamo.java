package Clases;

public class Prestamo {
    private String idObjeto, fechaPrestamo, fechaDevolucion, fechaPlazo, idPrestamista, idPrestatario, idReceptor;

    public Prestamo(String idObjeto, String fechaPrestamo, String fechaDevolucion, String fechaPlazo, String idPrestamista, String idPrestatario, String idReceptor) {

        this.idObjeto = idObjeto;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.fechaPlazo = fechaPlazo;
        this.idPrestamista = idPrestamista;
        this.idPrestatario = idPrestatario;
        this.idReceptor = idReceptor;

    }

    public String getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(String idObjeto) {
        this.idObjeto = idObjeto;
    }

    public String getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(String fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public String getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(String fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getFechaPlazo() {
        return fechaPlazo;
    }

    public void setFechaPlazo(String fechaPlazo) {
        this.fechaPlazo = fechaPlazo;
    }

    public String getIdPrestamista() {
        return idPrestamista;
    }

    public void setIdPrestamista(String idPrestamista) {
        this.idPrestamista = idPrestamista;
    }

    public String getIdPrestatario() {
        return idPrestatario;
    }

    public void setIdPrestatario(String idPrestatario) {
        this.idPrestatario = idPrestatario;
    }

    public String getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(String idReceptor) {
        this.idReceptor = idReceptor;
    }
}

package Clases;

public class Prestamo {
    private String prestamistaID, prestatarioID, objetoID ,fechaPrestamo, fechaEntrega, fechaDevolucion, receptorID, cantidad;

    public Prestamo(){
        //Constructor vacío
    }

    public Prestamo(String prestamistaID, String prestatarioID, String objetoID,
                    String fechaPrestamo, String fechaEntrega, String fechaDevolucion, String receptorID, String cantidad) {

        this.prestamistaID = prestamistaID;
        this.prestatarioID = prestatarioID;
        this.objetoID = objetoID;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaEntrega = fechaEntrega;
        this.fechaDevolucion = fechaDevolucion;
        this.receptorID = receptorID;
        this.cantidad = cantidad;
    }

    public String getPrestamistaID() {
        return prestamistaID;
    }

    public void setPrestamistaID(String prestamistaID) {
        this.prestamistaID = prestamistaID;
    }

    public String getPrestatarioID() {
        return prestatarioID;
    }

    public void setPrestatarioID(String prestatarioID) {
        this.prestatarioID = prestatarioID;
    }

    public String getObjetoID() {
        return objetoID;
    }

    public void setObjetoID(String objetoID) {
        this.objetoID = objetoID;
    }

    public String getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(String fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(String fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getReceptorID() {
        return receptorID;
    }

    public void setReceptorID(String receptorID) {
        this.receptorID = receptorID;
    }
}

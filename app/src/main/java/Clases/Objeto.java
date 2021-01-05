package Clases;

import java.io.Serializable;

public class Objeto implements Serializable
{
    private String key, nombre, estado, fechaRegistro, imageUrl, cantidad, categoria, UID;
    private String lastPrestatario, lastPrestamista, lastFechaDevolución, lastFechaPrestamo, lastReceptor;

    public Objeto(String nombre, String estado, String fechaRegistro, String imageUrl, String cantidad,
                  String categoria, String lastPrestatario, String lastPrestamista, String lastFechaDevolución,
                  String lastFechaPrestamo, String lastReceptor) {
        this.nombre = nombre;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.imageUrl = imageUrl;
        this.cantidad = cantidad;
        this.categoria = categoria;
        this.lastPrestatario = lastPrestatario;
        this.lastPrestamista = lastPrestamista;
        this.lastFechaDevolución = lastFechaDevolución;
        this.lastFechaPrestamo = lastFechaPrestamo;
        this.lastReceptor = lastReceptor;
    }

    public Objeto()
    {
        //Constructor vacío es necesario.
    }


    public String getLastPrestatario() {
        return lastPrestatario;
    }

    public void setLastPrestatario(String lastPrestatario) {
        this.lastPrestatario = lastPrestatario;
    }

    public String getLastPrestamista() {
        return lastPrestamista;
    }

    public void setLastPrestamista(String lastPrestamista) {
        this.lastPrestamista = lastPrestamista;
    }

    public String getLastFechaDevolución() {
        return lastFechaDevolución;
    }

    public void setLastFechaDevolución(String lastFechaDevolución) {
        this.lastFechaDevolución = lastFechaDevolución;
    }

    public String getLastFechaPrestamo() {
        return lastFechaPrestamo;
    }

    public void setLastFechaPrestamo(String lastFechaPrestamo) {
        this.lastFechaPrestamo = lastFechaPrestamo;
    }

    public String getLastReceptor() {
        return lastReceptor;
    }

    public void setLastReceptor(String lastReceptor) {
        this.lastReceptor = lastReceptor;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getUrlimage() {
        return imageUrl;
    }

    public void setUrlimage(String urlimage) {
        this.imageUrl = urlimage;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}

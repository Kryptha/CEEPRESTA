package Clases;

import java.io.Serializable;

public class Objeto implements Serializable
{
    private String key, nombre, estado, fechaRegistro, imageUrl, cantidad, categoria;

    public Objeto(String nombre, String estado, String fechaRegistro, String imageUrl, String cantidad, String categoria) {
        this.nombre = nombre;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.imageUrl = imageUrl;
        this.cantidad = cantidad;
        this.categoria = categoria;
    }

    public Objeto()
    {
        //Constructor vacío es necesario.
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

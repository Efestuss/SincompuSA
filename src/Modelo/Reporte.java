package Modelo;

import java.time.LocalDateTime;

/**
 *
 * @author Juan Ochoa
 */
public class Reporte {

    private String idProducto;
    private String nombre;
    private int cantidad;
    private double precio;
    private String categoria;
    private String proveedor;
    private String estado;
    private String fechaModificacion;

    public Reporte(String idProducto, String nombre, int cantidad, double precio, String categoria, String proveedor, String estado, String fechaModificacion) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.categoria = categoria;
        this.proveedor = proveedor;
        this.estado = estado;
        this.fechaModificacion = fechaModificacion;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
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

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(String fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

  

}

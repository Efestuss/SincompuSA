package Modelo;

/**
 *
 * @author Angie Buñay
 */

public class Inventario extends Producto {
    private String estado;

    // Constructor de Inventario
    public Inventario(String idProducto, String nombre, String categoria, int cantidad, double precio, String proveedor, String estado) {
        super(idProducto, nombre, categoria, cantidad, precio, proveedor); // Llama al constructor de la clase base (Producto)
        this.estado = estado;
    }

    // Getter y setter específico de Inventario
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

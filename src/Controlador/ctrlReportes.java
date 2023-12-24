package Controlador;

import Modelo.ConexionBD;
import Modelo.Proveedor;
import Modelo.Reporte;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Angelo Meza
 */
public class ctrlReportes {

    ConexionBD conexionBD = new ConexionBD();
    // Obtener una lista de proveedores desde la base de datos

    public List<Reporte> obtenerReportes() throws ClassNotFoundException {
        List<Reporte> reportes = new ArrayList<>();
        ConexionBD conexionBD = new ConexionBD();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conexionBD.openConnection(); // Abrir la conexión

            conn = conexionBD.getConnection();
            String sql = "SELECT Id_Producto, Nombre, Cantidad , Precio, Categoria, Proveedor, Estado, Fecha_Modificacion FROM Reportes";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            // Crear un formateador con el patrón deseado
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy, hh.mm.ss a");
            while (rs.next()) {
                String idProducto = rs.getString("Id_Producto");
                String nombre = rs.getString("Nombre");
                int cantidad = rs.getInt("Cantidad");
                double precio = rs.getDouble("Precio");
                String categoria = rs.getString("Categoria");
                String proveedor = rs.getString("Proveedor");
                String estado = rs.getString("Estado");
              
                  // Obtener la Fecha_Modificacion como un Timestamp
                java.sql.Timestamp fechaModificacionTimestamp = rs.getTimestamp("Fecha_Modificacion");

                // Convertir Timestamp a LocalDateTime
                LocalDateTime fechaModificacion = fechaModificacionTimestamp.toLocalDateTime();

                // Formatear la fecha utilizando el formateador
                String fechaFormateada = fechaModificacion.format(formatter);

                

                // Crear objeto Reportes y agregarlo a la lista
                reportes.add(new Reporte(idProducto, nombre, cantidad, precio, categoria, proveedor, estado, fechaFormateada));

            }
        } catch (SQLException e) {
            // Manejo de excepciones
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                    conexionBD.closeConnection(); // Cerrar la conexión
                }
            } catch (SQLException e) {
                // Manejo de excepciones en caso de error al cerrar la conexión
                e.printStackTrace();
            }
        }

        return reportes;
    }

}

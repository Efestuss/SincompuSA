package Controlador;

import Modelo.ConexionBD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Angelo Meza
 */
public class ctrlBodegueroAdmin {

    ConexionBD conexionBD = new ConexionBD();
    private static int ultimoIdUtilizado = 0;

    public void AgregarBodeguero(String nombreUsuario, String contrasena) {
        CallableStatement cs = null;

        try {
            // Obtener la conexión a la base de datos
            conexionBD.openConnection();
            Connection connection = conexionBD.getConnection();

            // Llamar al procedimiento almacenado AgregarBodeguero
            cs = connection.prepareCall("{call AgregarBodeguero(?, ?)}");
            cs.setString(1, nombreUsuario);
            cs.setString(2, contrasena);

            // Ejecutar el procedimiento almacenado
            cs.execute();

            System.out.println("Bodeguero agregado exitosamente.");
        } catch (SQLException | ClassNotFoundException e) {
            // Manejo de excepciones (puedes personalizar según tus necesidades)
            System.err.println("Error al agregar bodeguero: " + e.getMessage());
        } finally {
            // Cerrar recursos
            try {
                if (cs != null) {
                    cs.close();
                }
                conexionBD.closeConnection();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

 public int obtenerSiguienteValorSecuencia() throws SQLException {
        try {
            // Abre la conexión a la base de datos
            conexionBD.openConnection();

            // Obtiene la conexión actual
            Connection connection = conexionBD.getConnection();

            if (connection == null) {
                System.out.println("La conexión es nula");
                throw new SQLException("La conexión es nula");
            }

            // Obtener el siguiente valor de la secuencia directamente
            String sql = "SELECT usuarios_seq.NEXTVAL FROM DUAL";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int siguienteId = resultSet.getInt(1);
                    
                    // Asignar el siguiente ID al último ID utilizado
                    ultimoIdUtilizado = siguienteId;

                    return siguienteId;
                } else {
                    System.out.println("No se pudo obtener el siguiente valor de la secuencia.");
                    throw new SQLException("No se pudo obtener el siguiente valor de la secuencia.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener el siguiente valor de la secuencia: " + e.getMessage());
            throw new SQLException("Error al obtener el siguiente valor de la secuencia: " + e.getMessage(), e);
        } finally {
            try {
                // Cierra la conexión después de realizar la operación
                conexionBD.closeConnection();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    public static int getUltimoIdUtilizado() {
        return ultimoIdUtilizado;
    }

   
     public int obtenerUltimoIdAlmacenado() throws SQLException {
        try {
            // Abre la conexión a la base de datos
            conexionBD.openConnection();

            // Obtiene la conexión actual
            Connection connection = conexionBD.getConnection();

            if (connection == null) {
                System.out.println("La conexión es nula");
                throw new SQLException("La conexión es nula");
            }

            // Obtener el último ID almacenado en la base de datos
            String sql = "SELECT MAX(id) FROM usuarios";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    System.out.println("No se pudo obtener el último ID almacenado.");
                    throw new SQLException("No se pudo obtener el último ID almacenado.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener el último ID almacenado: " + e.getMessage());
            throw new SQLException("Error al obtener el último ID almacenado: " + e.getMessage(), e);
        } finally {
            try {
                // Cierra la conexión después de realizar la operación
                conexionBD.closeConnection();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}

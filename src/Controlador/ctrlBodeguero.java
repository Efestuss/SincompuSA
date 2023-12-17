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
public class ctrlBodeguero {

    ConexionBD conexionBD = new ConexionBD();

    public void actualizarUsuario(String nombreUsuario, String nuevoNombreUsuario, String nuevaContrasena) throws ClassNotFoundException, SQLException {
        // Obtener la conexión a la base de datos
        conexionBD.openConnection();
        Connection connection = conexionBD.getConnection();

        if (connection != null) {
            // Llamar al procedimiento almacenado
            String sql = "CALL actualizarUsuario(?, ?, ?)";
            try ( PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nombreUsuario);
                statement.setString(2, nuevoNombreUsuario);
                statement.setString(3, nuevaContrasena);
                statement.executeUpdate();
            }
        } else {
            // Manejar el caso en que la conexión sea nula
            System.err.println("La conexión a la base de datos es nula.");
        }

    }
}

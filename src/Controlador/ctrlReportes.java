package Controlador;

import Modelo.ConexionBD;
import Modelo.Reporte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.FontFormatting;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;

public class ctrlReportes {

    ConexionBD conexionBD = new ConexionBD();

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy, hh.mm.ss a");
            while (rs.next()) {
                String idProducto = rs.getString("Id_Producto");
                String nombre = rs.getString("Nombre");
                int cantidad = rs.getInt("Cantidad");
                double precio = rs.getDouble("Precio");
                String categoria = rs.getString("Categoria");
                String proveedor = rs.getString("Proveedor");
                String estado = rs.getString("Estado");
                java.sql.Timestamp fechaModificacionTimestamp = rs.getTimestamp("Fecha_Modificacion");
                LocalDateTime fechaModificacion = fechaModificacionTimestamp.toLocalDateTime();
                String fechaFormateada = fechaModificacion.format(formatter);
                reportes.add(new Reporte(idProducto, nombre, cantidad, precio, categoria, proveedor, estado, fechaFormateada));
            }
        } catch (SQLException e) {
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
                    conexionBD.closeConnection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return reportes;
    }

    public void exportarTablaAExcel(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como archivo Excel");
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".xlsx");
            }

            try ( XSSFWorkbook workbook = new XSSFWorkbook();  FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                Sheet sheet = workbook.createSheet("Datos");

                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);

                CellStyle dataCellStyle = workbook.createCellStyle();
                dataCellStyle.setBorderBottom(BorderStyle.THIN);
                dataCellStyle.setBorderTop(BorderStyle.THIN);
                dataCellStyle.setBorderRight(BorderStyle.THIN);
                dataCellStyle.setBorderLeft(BorderStyle.THIN);

                for (int i = 0; i < table.getColumnCount(); i++) {
                    sheet.setColumnWidth(i, 260 * 20);
                }

                TableModel model = table.getModel();
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(model.getColumnName(i));
                    cell.setCellStyle(headerCellStyle);
                }

                for (int i = 0; i < model.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue(model.getValueAt(i, j).toString());
                        cell.setCellStyle(dataCellStyle);

                        if (j == 6) {
                            String estado = model.getValueAt(i, j).toString();
                            applyConditionalFormatting(workbook, cell, estado);
                        }
                    }
                }

                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null, "Tabla exportada exitosamente a Excel", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error al exportar la tabla a Excel", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void applyConditionalFormatting(Workbook workbook, Cell cell, String estado) {
        SheetConditionalFormatting sheetCF = ((Sheet) cell.getSheet()).getSheetConditionalFormatting();

        // Crear regla para EN STOCK (Relleno verde)
        ConditionalFormattingRule ruleEnStock = sheetCF.createConditionalFormattingRule("UPPER(TRIM(A" + (cell.getRowIndex() + 1) + "))=\"EN STOCK\"");
        FontFormatting fontEnStock = ruleEnStock.createFontFormatting();
        fontEnStock.setFontColorIndex(IndexedColors.GREEN.index);

        // Aplicar regla para EN STOCK
        CellRangeAddress[] regionsEnStock = {CellRangeAddress.valueOf(cell.getAddress().formatAsString())};
        sheetCF.addConditionalFormatting(regionsEnStock, ruleEnStock);

        // Crear regla para AGOTADO (Relleno rojo)
        ConditionalFormattingRule ruleAgotado = sheetCF.createConditionalFormattingRule("UPPER(TRIM(A" + (cell.getRowIndex() + 1) + "))=\"AGOTADO\"");
        FontFormatting fontAgotado = ruleAgotado.createFontFormatting();
        fontAgotado.setFontColorIndex(IndexedColors.RED.index);

        // Aplicar regla para AGOTADO
        CellRangeAddress[] regionsAgotado = {CellRangeAddress.valueOf(cell.getAddress().formatAsString())};
        sheetCF.addConditionalFormatting(regionsAgotado, ruleAgotado);
    }

}

package DAO;

import modelo.irDefault;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Banco {
  public static List<irDefault> buscarBancos(String busco) throws SQLException {
    List<irDefault> defaults = new ArrayList<>();
    String query = "SELECT id, field_id, condition, json_value FROM ir_default";
    if (busco != null && !busco.trim().isEmpty()) {
      query += " WHERE condition LIKE ? OR json_value LIKE ?";
    }
    try (Connection conexion = Conexiondb.getConnection();
         PreparedStatement stmt = conexion.prepareStatement(query)) {
      if (busco != null && !busco.trim().isEmpty()) {
        String searchPattern = "%" + busco + "%";
        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);
      }
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        irDefault irDefault = new irDefault(
            rs.getInt("id"),
            rs.getInt("field_id"),
            rs.getString("condition"),
            rs.getString("json_value")
        );
        defaults.add(irDefault);
      }
    }
    return defaults;
  }
  public static List<irDefault> buscarBancosPorId(int id) throws SQLException {
    List<irDefault> defaults = new ArrayList<>();
    String query = "SELECT id, field_id, condition, json_value FROM ir_default WHERE id = ?";

    try (Connection conexion = Conexiondb.getConnection();
         PreparedStatement stmt = conexion.prepareStatement(query)) {

      stmt.setInt(1, id);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {

        irDefault irDefault = new irDefault(
            rs.getInt("id"),
            rs.getInt("field_id"),
            rs.getString("condition"),
            rs.getString("json_value")
        );
        defaults.add(irDefault);
      }
    }
    return defaults;
  }
}


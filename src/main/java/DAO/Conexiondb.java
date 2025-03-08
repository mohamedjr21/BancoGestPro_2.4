package DAO;

import java.sql.*;

public class Conexiondb {
  public static Connection connection = null;
  public static Connection getConnection() {

    String dbName = "odoo";
    String dbPort = "5432";
    String username = "odoo";
    String password = "odoo";

    try {
      Class.forName("org.postgresql.Driver");
      String url = "jdbc:postgresql://localhost:" + dbPort + "/" + dbName + "?user=" + username + "&password=" + password;
      connection = DriverManager.getConnection(url, username, password);
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    return connection;
  }
}
package sr.cliphist.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum ConnectionManager {
  INSTANCE;

  private static final String PASSWORD = "password";
  private static final String USERNAME = "java";
  private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/cliptest";
  private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  private Connection connection;

  private ConnectionManager() {
    this.connection = getConnection();
  }

  public Connection getConnection(){

    System.out.println("-------- MySQL JDBC Connection Testing ------------");

    try {
      Class.forName(JDBC_DRIVER);
    } catch (final ClassNotFoundException e) {
      System.out.println("Where is your " + JDBC_DRIVER);
      e.printStackTrace();
      return null;
    }

    System.out.println("JDBC Driver Registered! ");
    Connection connection = null;

    try {
      connection = DriverManager
      .getConnection(CONNECTION_STRING,USERNAME, PASSWORD);

    } catch (final SQLException e) {
      System.out.println("Connection Failed! Check output console");
      e.printStackTrace();
      return null;
    }

    if (connection != null) {
      System.out.println("You made it, take control your database now!");
    } else {
      System.out.println("Failed to make connection!");
    }

    return connection;

  }


}

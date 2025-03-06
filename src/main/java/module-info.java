module com.example.bancomfh {
  requires org.junit.jupiter.api;
  requires org.junit.jupiter.engine;
  requires org.junit.platform.commons;
  requires org.apiguardian.api;
  requires javafx.controls;
  requires javafx.fxml;
  requires java.sql;
  requires org.junit.platform.launcher;

  exports com.example.bancomfh;
  exports Controladores;
  //exports Tests;
  opens Controladores to javafx.fxml, org.junit.platform.commons;
  opens modelo to javafx.base;

}





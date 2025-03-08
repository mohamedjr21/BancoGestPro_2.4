package Controladores;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import DAO.Banco;
import DAO.Conexiondb;
import modelo.irDefault;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class HelloController {
  private final StringProperty searchText = new SimpleStringProperty("");
  private final BooleanProperty hasSelection = new SimpleBooleanProperty(false);
  private final ObservableList<irDefault> tableItems = FXCollections.observableArrayList();

  @FXML
  public TableView<irDefault> InicializarTabla;
  @FXML
  private TableColumn<irDefault, Integer> columna1;
  @FXML
  private TableColumn<irDefault, Integer> columna2;
  @FXML
  private TableColumn<irDefault, String> columna3;
  @FXML
  private TableColumn<irDefault, String> columna4;
  @FXML
  private TextField NombreIntro;
  @FXML
  private Button editarButton;
  @FXML
  private Button borrarButton;
  @FXML
  private Button BuscarButton;
  @FXML
  private Button altaBoton;
  @FXML
  private ImageView BuscarButon;
  @FXML
  private ImageView Borrar;

  @FXML
  public void initialize() {
    columna1.setCellValueFactory(new PropertyValueFactory<>("id"));
    columna2.setCellValueFactory(new PropertyValueFactory<>("fieldId"));
    columna3.setCellValueFactory(new PropertyValueFactory<>("condition"));
    columna4.setCellValueFactory(new PropertyValueFactory<>("jsonValue"));

    InicializarTabla.setItems(tableItems);

    Bindings.bindBidirectional(NombreIntro.textProperty(), searchText);

    InicializarTabla.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      hasSelection.set(newValue != null);
    });

    editarButton.disableProperty().bind(hasSelection.not());
    borrarButton.disableProperty().bind(hasSelection.not());


    try {
      actualizarTabla();
    } catch (SQLException e) {
      mostrarError("Error al cargar datos iniciales", e);
    }
  }


  @FXML
  public void BuscarButon(ActionEvent actionEvent) {
    final String termino = searchText.get().trim();

    Thread buscarThread = new Thread(() -> {
      try {
         List<irDefault> resultados;

        try {
          int id = Integer.parseInt(termino);
          resultados = Banco.buscarBancosPorId(id);
        } catch (NumberFormatException e) {
          resultados = Banco.buscarBancos(termino);
        }

        List<irDefault> finalResultados = resultados;
        Platform.runLater(() -> {
          tableItems.clear();
          tableItems.addAll(finalResultados);
        });
      } catch (SQLException e) {
        mostrarError("Error al realizar la búsqueda", e);
      }
    });

    buscarThread.setDaemon(true);
    buscarThread.start();
  }
  @FXML
  public void Altabuton(ActionEvent actionEvent) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/bancomfh/clienteAlta.fxml"));
      Parent root = fxmlLoader.load();

      Altacontroller controller = fxmlLoader.getController();
      controller.setControladorPrincipal(this);
      controller.limpiar();

      Stage scene = new Stage();
      scene.setTitle("Nueva Entidad");
      scene.setScene(new Scene(root));
      scene.setResizable(false);
      scene.showAndWait();
    } catch (IOException e) {
      mostrarError("Error al abrir la ventana de alta", e);
    }
  }

  @FXML
  public void EditarButon() {
    final irDefault selectedItem = InicializarTabla.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
      try {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/bancomfh/clienteAlta.fxml"));
        Parent root = fxmlLoader.load();

        Altacontroller controller = fxmlLoader.getController();
        controller.setControladorPrincipal(this);
        controller.cargarDatos(selectedItem);

        Stage stage = new Stage();
        stage.setTitle("Editar Registro");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.showAndWait();
      } catch (IOException e) {
        mostrarError("Error al abrir la ventana de edición", e);
      }
    } else {
      mostrarAdvertencia("Aviso", "Por favor seleccione un registro para editar");
    }
  }
  public void actualizarTabla() throws SQLException {
    Thread cargarDatosThread = new Thread(() -> {
      try {
        final List<irDefault> resultados = Banco.buscarBancos("");

        Platform.runLater(() -> {
          tableItems.clear();
          tableItems.addAll(resultados);
        });
      } catch (SQLException e) {
        mostrarError("Error al actualizar la tabla", e);
      }
    });

    cargarDatosThread.setDaemon(true);
    cargarDatosThread.start();
  }
  @FXML
  public void BorrarButon(ActionEvent actionEvent) {
    final irDefault selectedItem = InicializarTabla.getSelectionModel().getSelectedItem();

    if (selectedItem != null) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Confirmar borrado");
      alert.setHeaderText("¿Estas seguro de que quieres borrar este registro?");
      alert.setContentText("Esta acción no se puede deshacer.");

      Optional<ButtonType> result = alert.showAndWait();

      if (result.isPresent() && result.get() == ButtonType.OK) {
        Thread eliminarThread = new Thread(() -> {
          try {
            String query = "DELETE FROM ir_default WHERE id = ?";
            Connection conexion = Conexiondb.getConnection();
            PreparedStatement pstmt = conexion.prepareStatement(query);
            pstmt.setInt(1, selectedItem.getId());
            final int affected = pstmt.executeUpdate();

            Platform.runLater(() -> {
              if (affected > 0) {
                tableItems.remove(selectedItem);
                mostrarInformacion("Éxito", "Registro eliminado correctamente");
              } else {
                mostrarAdvertencia("Aviso", "No se pudo eliminar el registro");
              }
            });
          } catch (SQLException e) {
            mostrarError("Error al borrar el registro", e);
          }
        });

        eliminarThread.setDaemon(true);
        eliminarThread.start();
      }
    } else {
      mostrarAdvertencia("Aviso", "Seleccione un registro para borrar por favor");
    }
  }


  private void mostrarError(String titulo, Throwable exception) {
    Platform.runLater(() -> {
      Alert error = new Alert(Alert.AlertType.ERROR);
      error.setTitle("Error");
      error.setHeaderText(titulo);
      error.setContentText(exception.getMessage());
      error.show();
    });
  }

  private void mostrarAdvertencia(String titulo, String mensaje) {
    Platform.runLater(() -> {
      Alert aviso = new Alert(Alert.AlertType.WARNING);
      aviso.setTitle(titulo);
      aviso.setContentText(mensaje);
      aviso.show();
    });
  }

  private void mostrarInformacion(String titulo, String mensaje){
    Platform.runLater(() -> {
      Alert info = new Alert(Alert.AlertType.INFORMATION);
      info.setTitle(titulo);
      info.setContentText(mensaje);
      info.show();
    });
  }
}
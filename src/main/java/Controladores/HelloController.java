package Controladores;

import DAO.Banco;
import DAO.Conexiondb;
import javafx.scene.image.ImageView;
import modelo.irDefault;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class HelloController {

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
  private ImageView Buscar;
  @FXML
  private ImageView Borrar;

  @FXML
  public void BuscarButon(ActionEvent actionEvent) {
    final String searchTerm = NombreIntro.getText();

    Task<Void> tarea = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        try {

          List<irDefault> buscarBancos;
          try {

            int id = Integer.parseInt(searchTerm);
            buscarBancos = Banco.buscarBancosPorId(id);
          } catch (NumberFormatException e) {
            buscarBancos = Banco.buscarBancos(searchTerm);
          }

          final List<irDefault> BuscarBancos = buscarBancos;
          Platform.runLater(() -> {
            InicializarTabla.getItems().clear();
            InicializarTabla.getItems().addAll(BuscarBancos);
          });

        } catch (SQLException e) {

          System.err.println("Error de SQL al consultar:" + e.getMessage());
          Platform.runLater(() -> {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setContentText("Error al realizar la búsqueda,vuelva a intentarlo por favor.");
            error.show();
          });
        }
        return null;
      }
    };

    Thread hilo = new Thread(tarea);
    hilo.start();
  }


  @FXML
  public void Altabuton(ActionEvent actionEvent) {
    Thread altahilo = new Thread(() -> {
      try {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/bancomfh/clienteAlta.fxml"));
        Parent root = fxmlLoader.load();
        Altacontroller controller = fxmlLoader.getController();
        controller.setControladorPrincipal(this);
        controller.limpiar();

        Platform.runLater(() -> {
          Stage scene = new Stage();
          scene.setTitle("Nueva Entidad");
          scene.setScene(new Scene(root));
          scene.setResizable(false);
          scene.showAndWait();
        });
      } catch (IOException e) {
        Platform.runLater(() -> {
          Alert error = new Alert(Alert.AlertType.ERROR);
          error.setTitle("Error");
          error.setContentText("Error al cargar la ventana: " + e.getMessage());
          error.show();
        });
      }
    });
    altahilo.start();
  }

  @FXML
  public void EditarButon() {
    irDefault selectedItem = InicializarTabla.getSelectionModel().getSelectedItem();
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
        e.getMessage();
      }
    } else {
      Alert warning = new Alert(Alert.AlertType.WARNING);
      warning.setTitle("Aviso");
      warning.setContentText("Por favor seleccione un registro para editar");
      warning.show();
    }
  }
  @FXML
  public void initialize() {
    columna1.setCellValueFactory(new PropertyValueFactory<>("id"));
    columna2.setCellValueFactory(new PropertyValueFactory<>("fieldId"));
    columna3.setCellValueFactory(new PropertyValueFactory<>("condition"));
    columna4.setCellValueFactory(new PropertyValueFactory<>("jsonValue"));

    editarButton.setDisable(true);
    borrarButton.setDisable(true);

    InicializarTabla.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, valor) -> {
      boolean haySeleccion = valor != null;
      editarButton.setDisable(!haySeleccion);
      borrarButton.setDisable(!haySeleccion);

      if (haySeleccion) {
        editarButton.setFocusTraversable(true);
        borrarButton.setFocusTraversable(true);
        editarButton.requestFocus();
      }
    });
  }


  public void actualizarTabla() throws SQLException {
    InicializarTabla.getItems().clear();
    List<irDefault> buscarBancos = Banco.buscarBancos("");
    InicializarTabla.getItems().addAll(buscarBancos);
    System.out.println("Tabla ha sido actualizada correctamente");
  }
  @FXML
  public void BorrarButon(ActionEvent actionEvent) {

    irDefault selectedItem = InicializarTabla.getSelectionModel().getSelectedItem();

    if (selectedItem != null) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Confirmar borrado");
      alert.setHeaderText("¿Estas seguro de que quieres borrar este registro?");
      alert.setContentText("Esta acción no se puede deshacer.");

      Optional<ButtonType> result = alert.showAndWait();

      if (result.isPresent() && result.get() == ButtonType.OK) {
        try {
          String query = "DELETE FROM ir_default WHERE id = ?";
          Connection conexion = Conexiondb.getConnection();
          PreparedStatement pstmt = conexion.prepareStatement(query);
          pstmt.setInt(1, selectedItem.getId());
          pstmt.executeUpdate();

          InicializarTabla.getItems().remove(selectedItem);

          //Alert borrado = new Alert(Alert.AlertType.INFORMATION);
          //borrado.setTitle("Éxito");
          //borrado.setContentText("Registro eliminado correctamente");
          //borrado.show();

        } catch (SQLException e) {
          Alert error = new Alert(Alert.AlertType.ERROR);
          error.setTitle("Error");
          error.setContentText("Error al borrar el registro,vuelva a intentarlo por favor: " + e.getMessage());
          error.show();
        }
      }
    } else {
      Alert aviso = new Alert(Alert.AlertType.WARNING);
      aviso.setTitle("Aviso");
      aviso.setContentText(" seleccione un registro para borrar por favor");
      aviso.show();
    }
  }
}

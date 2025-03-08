package Controladores;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import DAO.Conexiondb;
import modelo.irDefault;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Altacontroller {
  private final StringProperty idText = new SimpleStringProperty("");
  private final StringProperty companyIdText = new SimpleStringProperty("");
  private final StringProperty conditionText = new SimpleStringProperty("");
  private final StringProperty jsonValueText = new SimpleStringProperty("");
  private final StringProperty registroText = new SimpleStringProperty("NUEVO REGISTRO");
  private final BooleanProperty camposValidos = new SimpleBooleanProperty(false);
  private final BooleanProperty editando = new SimpleBooleanProperty(false);
  private final BooleanProperty idValido = new SimpleBooleanProperty(false);
  private final BooleanProperty companyIdValido = new SimpleBooleanProperty(false);
  private final BooleanProperty conditionValido = new SimpleBooleanProperty(false);
  private final BooleanProperty jsonValueValido = new SimpleBooleanProperty(false);
  @FXML
  private TextField idField;
  @FXML
  private TextField companyIdField;
  @FXML
  private TextField conditionField;
  @FXML
  private TextField jsonValueField;
  @FXML
  private Label registro;
  @FXML
  private Button botonAceptar;
  @FXML
  private Button butonCancelar;
  @FXML
  private irDefault guardarparaEdit;
  @FXML
  private HelloController controladorPrincipal;


  @FXML
  public void initialize() {
    Bindings.bindBidirectional(idField.textProperty(), idText);
    Bindings.bindBidirectional(companyIdField.textProperty(), companyIdText);
    Bindings.bindBidirectional(conditionField.textProperty(), conditionText);
    Bindings.bindBidirectional(jsonValueField.textProperty(), jsonValueText);
    Bindings.bindBidirectional(registro.textProperty(), registroText);

    idField.editableProperty().bind(editando.not());

    idField.styleProperty().bind(
        //Le he puesto el rojo si se introduce una letra o algún caracter raro donde no corresponde
        Bindings.when(idValido.not().and(idText.isNotEmpty()))
            .then("-fx-border-color: red;")
            .otherwise("")
    );

    companyIdField.styleProperty().bind(
        Bindings.when(companyIdValido.not().and(companyIdText.isNotEmpty()))
            .then("-fx-border-color: red;")
            .otherwise("")
    );

    idText.addListener((obs, old, nuevo) -> validarCampoId());
    companyIdText.addListener((obs, old, nuevo) -> validarCampoCompanyId());
    conditionText.addListener((obs, old, nuevo) -> validarCampoCondition());
    jsonValueText.addListener((obs, old, nuevo) -> validarCampoJsonValue());

    camposValidos.bind(
        idValido.and(companyIdValido).and(conditionValido).and(jsonValueValido)
    );

    botonAceptar.disableProperty().bind(camposValidos.not());
  }
  private void validarCampoId() {
    String valor = idText.get().trim();
    if (valor.isEmpty()) {
      idValido.set(false);
      return;
    }
    try {
      int id = Integer.parseInt(valor);
      if (id <= 0) {
        idValido.set(false);
      } else {
        idValido.set(true);
      }
    } catch (NumberFormatException e) {
      idValido.set(false);
    }
  }

  private void validarCampoCompanyId() {
    String valor = companyIdText.get().trim();
    if (valor.isEmpty()) {
      companyIdValido.set(false);
      return;
    }

    try {
      int id = Integer.parseInt(valor);
      if (id <= 0) {
        companyIdValido.set(false);
      } else {
        companyIdValido.set(true);
      }
    } catch (NumberFormatException e) {
      companyIdValido.set(false);
    }
  }

  private void validarCampoCondition() {
    String valor = conditionText.get().trim();
    conditionValido.set(!valor.isEmpty());
  }
  private void validarCampoJsonValue() {
    String valor = jsonValueText.get().trim();
    jsonValueValido.set(!valor.isEmpty());
  }
  public void setControladorPrincipal(HelloController controlador) {
    this.controladorPrincipal = controlador;
  }
  private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
    Platform.runLater(() -> {
      Alert alert = new Alert(tipo);
      alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : "Éxito");
      alert.setContentText(mensaje);
      alert.showAndWait();
    });
  }
  private void cerrarVentana() {
    Stage stage = (Stage) idField.getScene().getWindow();
    if (stage != null) {
      stage.close();
    }
  }
  public void limpiar() {
    // Limpiar los campos de texto
    idText.set("");
    companyIdText.set("");
    conditionText.set("");
    jsonValueText.set("");
    registroText.set("NUEVO REGISTRO");
    editando.set(false);
    guardarparaEdit = null;
    // Validando campos
    validarCampoId();
    validarCampoCompanyId();
    validarCampoCondition();
    validarCampoJsonValue();
  }
  public void cargarDatos(irDefault registro) {
    idText.set(String.valueOf(registro.getId()));
    companyIdText.set(String.valueOf(registro.getFieldId()));
    conditionText.set(registro.getCondition());
    jsonValueText.set(registro.getJsonValue());
    registroText.set("EDITANDO REGISTRO");
    editando.set(true);
    guardarparaEdit = registro;
    //Validando
    validarCampoId();
    validarCampoCompanyId();
    validarCampoCondition();
    validarCampoJsonValue();
  }


  @FXML
  public void ButonAceptar(ActionEvent actionEvent) {
    if (!camposValidos.get()) {
      mostrarAlerta("Por favor, complete correctamente todos los campos", Alert.AlertType.WARNING);
      return;
    }

    try {
      final int id = Integer.parseInt(idText.get().trim());
      final int fieldId = Integer.parseInt(companyIdText.get().trim());
      final String condition = conditionText.get().trim();
      final String jsonValue = jsonValueText.get().trim();

      final irDefault registro = new irDefault(id, fieldId, condition, jsonValue);
      final boolean isEditando = editando.get();

      Thread guardarThread = new Thread(() -> {
        try {
          String sql = isEditando
              ? "UPDATE ir_default SET field_id=?, condition=?, json_value=? WHERE id=?"
              : "INSERT INTO ir_default (id, field_id, condition, json_value) VALUES (?, ?, ?, ?)";

          Connection conexion = Conexiondb.getConnection();
          PreparedStatement stmt = conexion.prepareStatement(sql);

          if (isEditando) {
            stmt.setInt(1, registro.getFieldId());
            stmt.setString(2, registro.getCondition());
            stmt.setString(3, registro.getJsonValue());
            stmt.setInt(4, registro.getId());
          } else {
            stmt.setInt(1, registro.getId());
            stmt.setInt(2, registro.getFieldId());
            stmt.setString(3, registro.getCondition());
            stmt.setString(4, registro.getJsonValue());
          }
          final int actualizado = stmt.executeUpdate();

          Platform.runLater(() -> {
            if (actualizado>0) {
              mostrarAlerta(
                  isEditando ? "Registro actualizado correctamente" : "Registro insertado correctamente",
                  Alert.AlertType.INFORMATION
              );

              try {
                controladorPrincipal.actualizarTabla();
                cerrarVentana();
              } catch (SQLException e) {
                mostrarAlerta("Error al actualizar la tabla: " + e.getMessage(), Alert.AlertType.ERROR);
              }
            } else {
              mostrarAlerta(
                  "No se pudo " + (isEditando ? "actualizar" : "insertar") + " el registro",
                  Alert.AlertType.WARNING
              );
            }
          });
        } catch (SQLException e) {
          mostrarAlerta("Ha surgido un error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
      });
      guardarThread.setDaemon(true);
      guardarThread.start();

    } catch (NumberFormatException e) {
      mostrarAlerta("Los campos Id y id empresa son obligatorios y deben ser numeros", Alert.AlertType.ERROR);
    }
  }
  @FXML
  public void ButonCancelar(ActionEvent actionEvent) {
    cerrarVentana();
  }
}
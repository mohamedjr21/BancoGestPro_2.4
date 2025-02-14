package Controladores;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import java.io.IOException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class AltacontrollerTest {

  private Pane mainroot;
  private Stage mainstage;

  @BeforeAll
  public static void initJavaFX() {
  }

  @Start
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/bancomfh/hello-view.fxml"));
    mainroot = fxmlLoader.load();
    stage.setTitle("Banco");
    Scene scene = new Scene(mainroot, 640, 600);
    stage.setScene(scene);
    stage.show();
    stage.toFront();
  }


  @Test
  void testVista(FxRobot robot) {
    FxAssert.verifyThat(mainroot, Pane::isVisible);
  }

  @Test
  void TestAltaClienteCamposVacios(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(1500);
    robot.clickOn("#altaBoton");
    robot.sleep(1000);
    robot.clickOn("#botonAceptar");
    robot.sleep(500);
    Optional<DialogPane> dialogPane = robot.lookup(".dialog-pane").tryQueryAs(DialogPane.class);
  }

  @Test
  void TestAltaCliente1Campo(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);
    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());
    robot.clickOn("#altaBoton");
    robot.sleep(1000);
    robot.write("0jdsahfsdhajshfhsdhfsdfdsf");
    robot.clickOn("#companyIdField");
    robot.clickOn("#botonAceptar");
    robot.sleep(500);
  }

  @Test
  void TestAltaCliente2Campos(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);
    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());
    robot.clickOn("#altaBoton");
    robot.sleep(1000);
    robot.clickOn("#companyIdField");
    robot.write("kjfsdafkjsdjfjasdf");
    robot.clickOn("#conditionField");
    robot.write("murgi");
    robot.clickOn("#botonAceptar");
    robot.sleep(500);
  }

  @Test
  void TestAltaClienteCon3Campos(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);
    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());
    robot.clickOn("#altaBoton");
    robot.sleep(1000);
    robot.write("0jdsahfsdhajshfhsdhfsdfdsf");
    robot.clickOn("#companyIdField");
    robot.write("kjfsdafkjsdjfjasdf");
    robot.clickOn("#conditionField");
    robot.write("murgi");
    robot.clickOn("#botonAceptar");
    robot.sleep(500);
  }

  @Test
  void TestAltaCliente4CamposNoSeActivaBUTON(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);
    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());
    robot.clickOn("#altaBoton");
    robot.sleep(1000);
    robot.write("0jdsahfsdhajshfhsdhfsdfdsf");
    robot.clickOn("#companyIdField");
    robot.write("kjfsdafkjsdjfjasdf");
    robot.clickOn("#conditionField");
    robot.write("murgi");
    robot.clickOn("#jsonValueField");
    robot.write("hola nuevo ");
    robot.clickOn("#botonAceptar");
    robot.sleep(500);
    robot.clickOn("#BuscarButton");
  }

  @Test
  void TestAltaCliente(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);
    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());
    robot.clickOn("#altaBoton");
    robot.sleep(1000);
    robot.write("3");
    robot.clickOn("#companyIdField");
    robot.write("1");
    robot.clickOn("#conditionField");
    robot.write("las norias fc");
    robot.clickOn("#jsonValueField");
    robot.write("norias fc ");
    robot.clickOn("#botonAceptar");
    robot.sleep(500);
    robot.clickOn("#BuscarButton");
    robot.clickOn("6");
    robot.sleep(1000);

  }

  @Test
  void TestAltaClienteComprobarDuplicidad1(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);
    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());
    robot.clickOn("#altaBoton");
    robot.sleep(1000);
    robot.write("6");
    robot.clickOn("#companyIdField");
    robot.write("1");
    robot.clickOn("#conditionField");
    robot.write("Almeria fc");
    robot.clickOn("#jsonValueField");
    robot.write("hola Almeria fc ");
    robot.clickOn("#botonAceptar");
    robot.sleep(1000);
    WaitForAsyncUtils.waitForFxEvents();
    Optional<DialogPane> dialogPane = robot.lookup(".dialog-pane").tryQueryAs(DialogPane.class);
    assertTrue(dialogPane.isPresent());
    assertTrue(dialogPane.get().isVisible());

    robot.clickOn("Aceptar");
    robot.sleep(500);
  }


}
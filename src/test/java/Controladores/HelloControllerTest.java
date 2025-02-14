package Controladores;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class HelloControllerTest {

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
  void testBuscarButton(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);
    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());
  }

  @Test
  void testEliminarRegistroRelacionadosOtrasTablas(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);
    robot.clickOn("4");
    robot.clickOn("#borrarButton");
    robot.sleep(500);
    Node mensajeAlerta = robot.lookup(".dialog-pane").query();
    assertTrue(mensajeAlerta.isVisible());
    robot.clickOn("Aceptar");

  }

  @Test
  void testBuscarButtonPorCondition(FxRobot robot) {
    FxAssert.verifyThat(mainroot, Parent::isVisible);
    TextField nombreIntro = robot.lookup("#NombreIntro").query();
    robot.write("real madrid");
    Button buscarButton = (Button) robot.lookup("#BuscarButton").query();
    robot.clickOn(buscarButton);
    robot.sleep(1000);

    TableView<?> tableView = robot.lookup("#InicializarTabla").query();
    assertTrue(tableView.getItems().size() > 0, "tablaa");
  }

  @Test
  void testBuscarButtonPorValorJson(FxRobot robot) {
    FxAssert.verifyThat(mainroot, Parent::isVisible);
    TextField nombreIntro = robot.lookup("#NombreIntro").query();
    robot.write("hola selectividad. hola fuente nuevaa");
    Button buscarButton = (Button) robot.lookup("#BuscarButton").query();
    robot.clickOn(buscarButton);
    robot.sleep(1000);

    TableView<?> tableView = robot.lookup("#InicializarTabla").query();
    assertTrue(tableView.getItems().size() > 0, "tablaa");

  }

  @Test
  void testBuscarButtonPorId(FxRobot robot) {
    FxAssert.verifyThat(mainroot, Parent::isVisible);

    TextField nombreIntro = robot.lookup("#NombreIntro").query();
    robot.clickOn(nombreIntro).write("1");
    robot.clickOn("#BuscarButton");

    WaitForAsyncUtils.waitForFxEvents();
    robot.sleep(2000);

    TableView<?> tableView = robot.lookup("#InicializarTabla").query();
    assertNotNull(tableView);

    WaitForAsyncUtils.waitForFxEvents();
    assertTrue(tableView.getItems().size() > 0);
  }


  @Test
  void testEditarRegistroConCamposVacios(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);

    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());

    robot.clickOn("1");
    robot.clickOn("#editarButton");
    robot.sleep(1000);

    //Dejo el campo vacío y hago  clic en Aceptar, y no me va a dejar
    //aceptar la edición del registro
    //porque tienen que estar todos los datos rellenados

    robot.clickOn("#conditionField").eraseText(15);
    robot.clickOn("#botonAceptar");

  }

  @Test
  void testEditarRegistro1Campo(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);

    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());

    robot.clickOn("1");

    robot.clickOn("#editarButton");
    robot.sleep(2000);
    robot.clickOn("#conditionField");
    robot.eraseText(15);
    robot.write("almeria fc");
    robot.clickOn("#botonAceptar");
  }


  @Test
  void testEditarRegistro2Campos(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);
    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());
    robot.clickOn("9");
    robot.clickOn("#editarButton");
    robot.sleep(1000);
    robot.lookup("#companyIdField").query();
    robot.clickOn("#companyIdField");
    robot.eraseText(10);
    robot.write("8");
    robot.clickOn("#conditionField");
    robot.eraseText(20);
    robot.write("FACEBOOK");

    robot.clickOn("#botonAceptar");

  }



  @Test
  void testEditarRegistroTodosLosCampos(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);
    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());
    robot.clickOn("1");
    robot.clickOn("#editarButton");
    robot.sleep(1000);
    robot.lookup("#companyIdField").query();
    robot.clickOn("#companyIdField");
    robot.eraseText(10);
    robot.write("3");
    robot.clickOn("#conditionField");
    robot.eraseText(20);
    robot.write("malaga fc");
    robot.clickOn("#jsonValueField");
    robot.eraseText(10);
    robot.write("hola");
    robot.clickOn("#botonAceptar");
  }


  @Test
  void TestBorrarRegistros(FxRobot robot) {
    robot.clickOn("#BuscarButton");
    robot.sleep(500);
    assertTrue(robot.lookup("#InicializarTabla").query().isVisible());

    robot.clickOn("4");
    robot.clickOn("#borrarButton");
    robot.sleep(500);

    Node mensajeAlerta = robot.lookup(".dialog-pane").query();
    assertTrue(mensajeAlerta.isVisible());
    robot.clickOn("Aceptar");

    robot.lookup(".button").queryAll().stream()
        .filter(button -> button instanceof Button && ((Button) button).getText().equals("Aceptar"))
        .findFirst()
        .ifPresent(robot::clickOn);

    robot.sleep(500);

    robot.clickOn("#BuscarButton");
    robot.sleep(1000);

  }
}




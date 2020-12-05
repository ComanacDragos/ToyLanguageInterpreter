package View.GUI;

import View.GUI.GUIController.ControllerSelectProgram;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class GUIView extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader selectProgramLoader = new FXMLLoader();
        selectProgramLoader.setLocation(getClass().getResource("FXML/SelectProgram.fxml"));
        AnchorPane root = selectProgramLoader.load();

        ControllerSelectProgram controller = selectProgramLoader.getController();

        controller.setParentStage(primaryStage);

        primaryStage.setTitle("Interpreter");
        primaryStage.setScene(new Scene(root, 720, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

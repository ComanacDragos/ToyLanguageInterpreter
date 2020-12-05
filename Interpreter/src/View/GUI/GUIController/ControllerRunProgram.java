package View.GUI.GUIController;

import Controller.Controller;
import Model.ADTs.MyIStack;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Values.IValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ControllerRunProgram {
    @FXML
    TableColumn<Pair<Integer, IValue>, Integer> heapAddressColumn;
    @FXML
    TableColumn<Pair<Integer, IValue>, IValue> heapValueColumn;
    @FXML
    TableColumn<Pair<String, IValue>, String> symbolsTableNameColumn;
    @FXML
    TableColumn<Pair<String, IValue>, IValue> symbolsTableValueColumn;
    @FXML
    Label numberOfProgramStatesLabel;
    @FXML
    TableView<Pair<Integer, IValue>> heapTableView;
    @FXML
    ListView<Integer> programIdsListView;
    @FXML
    ListView<IStatement> executionStackListView;
    @FXML
    ListView<String> outListView;
    @FXML
    ListView<String> fileTableListView;
    @FXML
    TableView<Pair<String, IValue>> symbolsTableView;

    Stage parentStage;
    Scene selectProgramsScene;

    Controller controller;

    @FXML
    void initialize(){
        this.programIdsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public void setSelectProgramsScene(Scene selectProgramsScene) {
        this.selectProgramsScene = selectProgramsScene;
    }

    public void setController(Controller controller) {
        this.controller = controller;
        this.controller.emptyLogFile();
    }

    void update(){
        this.setNumberOfProgramStatesLabel();
        this.setProgramIdsListView();
        this.setExecutionStackListView();
    }

    void setNumberOfProgramStatesLabel(){
        this.numberOfProgramStatesLabel.setText("Number of program states: " + this.controller.getPrograms().size());
    }

    void setProgramIdsListView(){
        ObservableList<Integer> programIds = FXCollections.observableArrayList();

        programIds.addAll(
          this.controller
          .getPrograms()
          .stream()
          .map(ProgramState::getProgramId)
          .collect(Collectors.toList())
        );

        this.programIdsListView.setItems(programIds);
    }

    void setExecutionStackListView(){
        Optional<ProgramState> program = this.controller.getProgram(this.getSelectedProgramId());

        if(program.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Program is done");
            alert.showAndWait();
            return;
        }
        ObservableList<IStatement> statements = FXCollections.observableArrayList();

        statements.addAll(
                program.get().getExecutionStack()
                .stream()
                .collect(Collectors.toList())
        );

        this.executionStackListView.setItems(statements);
    }

    void setSymbolsTableView(){
        Optional<ProgramState> program = this.controller.getProgram(this.getSelectedProgramId());

        if(program.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Program is done");
            alert.showAndWait();
            return;
        }

    }

    Integer getSelectedProgramId(){
        Integer id = this.programIdsListView.getSelectionModel().getSelectedItem();

        if(Objects.nonNull(id))
            return id;
        return this.programIdsListView.getItems().get(0);
    }

    public void handleRunAnotherProgram(ActionEvent actionEvent) {
        this.parentStage.setScene(this.selectProgramsScene);
    }

    public void handleRunOneStep(ActionEvent actionEvent) {

    }

    public void handleSelectProgram(MouseEvent mouseEvent) {
    }
}

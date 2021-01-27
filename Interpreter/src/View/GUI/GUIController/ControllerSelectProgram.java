package View.GUI.GUIController;

import Controller.Controller;
import Exceptions.MyException;
import Model.ADTs.*;
import Model.Expressions.BinaryExpressions.ArithmeticExpression;
import Model.Expressions.BinaryExpressions.LogicExpression;
import Model.Expressions.BinaryExpressions.MulExpression;
import Model.Expressions.BinaryExpressions.RelationalExpression;
import Model.Expressions.UnaryExpressions.ReadHeapExpression;
import Model.Expressions.ValueExpression;
import Model.Expressions.VariableExpression;
import Model.ProgramState;
import Model.Statements.*;
import Model.Statements.ControlFlowStatements.ForkStatement;
import Model.Statements.ControlFlowStatements.IfStatement;
import Model.Statements.ControlFlowStatements.WhileStatement;
import Model.Statements.ExtraStatements.*;
import Model.Statements.FileStatements.CloseReadFileStatement;
import Model.Statements.FileStatements.OpenReadFileStatement;
import Model.Statements.FileStatements.ReadFileStatement;
import Model.Statements.HeapStatements.NewStatement;
import Model.Statements.HeapStatements.WriteHeapStatement;
import Model.Types.BoolType;
import Model.Types.IntType;
import Model.Types.ReferenceType;
import Model.Types.StringType;
import Model.Values.BoolValue;
import Model.Values.IValue;
import Model.Values.IntValue;
import Model.Values.StringValue;
import Repository.IRepository;
import Repository.Repository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ControllerSelectProgram {
    @FXML
    private ListView<String> programsListView;

    private Map<String, IStatement> programsDescriptions;
    private Stage parentStage;

    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    @FXML
    public void initialize(){
        this.setupPrograms();

        ObservableList<String> programs = FXCollections.observableArrayList();

        AtomicInteger currentIndex = new AtomicInteger(1);
        this.programsDescriptions.forEach((key, value) -> programs.add((currentIndex.getAndIncrement()) + ". " + key));

        this.programsListView.setItems(programs);
        this.programsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void handleSelectProgram(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            try{
                String key =  this.programsListView.getSelectionModel().getSelectedItem();
                IStatement statement = this.programsDescriptions.get(key.substring(key.indexOf(' ') + 1)).deepCopy();

                try{
                    statement.typeCheck(new MyDictionary<>());
                }
                catch (MyException exception){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Type check error");
                    alert.setContentText(exception.getMessage());
                    alert.showAndWait();
                    return;
                }

                MyIStack<IStatement> executionStack = new MyStack<>();
                executionStack.push(statement);
                MyIDictionary<String, IValue> symbolsTable = new MyDictionary<>();
                MyIList<IValue> out = new MyList<>();
                MyIDictionary<String, BufferedReader> fileTable = new MyDictionary<>();
                MyHeap heap = new MyHeap();

                ProgramState newProgram = new ProgramState(executionStack, symbolsTable, out, fileTable, heap, statement);

                IRepository repository = new Repository("src/Files/log" + (this.programsListView.getSelectionModel().getSelectedIndices().get(0) + 1) + ".txt");
                Controller interpreterController = new Controller(repository);
                interpreterController.addProgramState(newProgram);

                Parent root;
                FXMLLoader runProgramLoader = new FXMLLoader();
                runProgramLoader.setLocation(getClass().getResource("../FXML/RunProgram.fxml"));
                root = runProgramLoader.load();

                ControllerRunProgram controller = runProgramLoader.getController();

                controller.setParentStage(this.parentStage);
                controller.setSelectProgramsScene(this.parentStage.getScene());
                controller.setController(interpreterController);
                //controller.update();

                interpreterController.addObserver(controller);
                interpreterController.notyfiObservers();

                Scene scene = new Scene(root, 1024, 720);
                //scene.getStylesheets().add(getClass().getResource("../CSS/main.css").toExternalForm());

                this.parentStage.setScene(scene);
                this.parentStage.show();
            }
            catch (Exception exc){
                exc.printStackTrace();
                System.exit(1);
            }

        }
    }

    void setupPrograms(){
        this.programsDescriptions = new LinkedHashMap<>();
        
        IStatement ex1 = new CompoundStatement(
                new VariableDeclarationStatement("v", new IntType()),
                new CompoundStatement(
                        new AssignStatement("v", new ValueExpression(new IntValue(2))),
                        new PrintStatement(new VariableExpression("v"))
                )
        );
        this.programsDescriptions.put("int v; v=2; Print(v)", ex1);

        IStatement ex2 = new CompoundStatement(
                new VariableDeclarationStatement("a", new IntType()),
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement("b", new IntType()),
                                new AssignStatement("a", new ArithmeticExpression(
                                        new ValueExpression(new IntValue(2)),
                                        new ArithmeticExpression(
                                                new ValueExpression(new IntValue(3)),
                                                new ValueExpression(new IntValue(5)),
                                                ArithmeticExpression.ArithmeticOperation.MULTIPLICATION
                                        ),
                                        ArithmeticExpression.ArithmeticOperation.ADDITION
                                ))
                        ),
                        new CompoundStatement(
                                new AssignStatement("b", new ArithmeticExpression(
                                        new VariableExpression("a"),
                                        new ValueExpression(new IntValue(1)),
                                        ArithmeticExpression.ArithmeticOperation.ADDITION
                                )),
                                new PrintStatement(new VariableExpression("b"))
                        )
                )
        );
        this.programsDescriptions.put("int a; int b; a=2+3*5; b=a+1; Print(b)", ex2);


        IStatement ex3 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement("a", new BoolType()),
                                new VariableDeclarationStatement("v", new IntType())
                        ),
                        new AssignStatement("a", new ValueExpression(new BoolValue(true)))
                ),
                new CompoundStatement(
                        new IfStatement(
                                new VariableExpression("a"),
                                new AssignStatement("v", new ValueExpression(new IntValue(2))),
                                new AssignStatement("v", new ValueExpression(new IntValue(3)))
                        ),
                        new PrintStatement(new VariableExpression("v"))
                )
        );

        this.programsDescriptions.put("bool a; int v; a=true; (If a Then v=2 Else v=3); Print(v)", ex3);

        IStatement ex4 = new CompoundStatement(
                new VariableDeclarationStatement("a", new IntType()),
                new CompoundStatement(
                        new AssignStatement("a", new ArithmeticExpression(
                                new ValueExpression(new IntValue(10)),
                                new ValueExpression(new IntValue(0)),
                                ArithmeticExpression.ArithmeticOperation.DIVISION
                        )),
                        new PrintStatement(new VariableExpression("a"))
                )
        );
        this.programsDescriptions.put("int a; a=10/0; Print(a)", ex4);

        IStatement ex5 = new CompoundStatement(
                new VariableDeclarationStatement("a", new IntType()),
                new CompoundStatement(
                        new AssignStatement("a", new ArithmeticExpression(
                                new ValueExpression(new IntValue(10)),
                                new ValueExpression(new IntValue(2)),
                                ArithmeticExpression.ArithmeticOperation.DIVISION
                        )),
                        new PrintStatement(new VariableExpression("b"))
                )
        );
        this.programsDescriptions.put("int a; a=10/2; Print(b)", ex5);

        IStatement ex6 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement("a", new BoolType()),
                                new AssignStatement("a", new ValueExpression(new BoolValue(true)))
                        ),
                        new VariableDeclarationStatement("b", new BoolType())
                ),
                new CompoundStatement(
                        new PrintStatement(
                                new LogicExpression(
                                        new VariableExpression("a"),
                                        new VariableExpression("b"),
                                        LogicExpression.LogicOperand.AND
                                )
                        ),
                        new PrintStatement(
                                new LogicExpression(
                                        new VariableExpression("a"),
                                        new VariableExpression("b"),
                                        LogicExpression.LogicOperand.OR
                                )
                        )
                )
        );
        this.programsDescriptions.put("bool a; a = true; bool b; Print(a && b); Print(a || b)", ex6);

        IStatement ex7 = new CompoundStatement(
                new CompoundStatement(
                        new VariableDeclarationStatement("varf", new StringType()),
                        new CompoundStatement(
                                new AssignStatement("varf", new ValueExpression(new StringValue("src/Files/test.in"))),
                                new CompoundStatement(
                                        new OpenReadFileStatement(new ValueExpression(new StringValue("src/Files/test.in"))),
                                        new VariableDeclarationStatement("varc", new IntType())
                                )
                        )
                ),
                new CompoundStatement(
                        new CompoundStatement(
                                new ReadFileStatement(new ValueExpression(new StringValue("src/Files/test.in")), "varc"),
                                new PrintStatement(new VariableExpression("varc"))
                        ),
                        new CompoundStatement(
                                new CompoundStatement(
                                        new ReadFileStatement(new ValueExpression(new StringValue("src/Files/test.in")), "varc"),
                                        new PrintStatement(new VariableExpression("varc"))
                                ),
                                new CloseReadFileStatement(new ValueExpression(new StringValue("src/Files/test.in")))
                        )
                )
        );
        this.programsDescriptions.put("string varf;varf=\"test.in\";openRFile(varf);int varc; readFile(varf,varc);print(varc); readFile(varf,varc);print(varc);closeRFile(varf)", ex7);

        IStatement ex8 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement("a", new IntType()),
                                new VariableDeclarationStatement("b", new IntType())
                        ),
                        new CompoundStatement(
                                new AssignStatement("a", new ValueExpression(new IntValue(5))),
                                new AssignStatement("b", new ValueExpression(new IntValue(10)))
                        )
                ),
                new CompoundStatement(
                        new CompoundStatement(
                                new PrintStatement(
                                        new RelationalExpression(
                                                new VariableExpression("a"),
                                                new VariableExpression("b"),
                                                RelationalExpression.RelationalOperation.NOT_EQUAL
                                        )
                                ),
                                new PrintStatement(
                                        new RelationalExpression(
                                                new VariableExpression("a"),
                                                new VariableExpression("b"),
                                                RelationalExpression.RelationalOperation.LESS_THAN
                                        )
                                )
                        ),
                        new CompoundStatement(
                                new PrintStatement(
                                        new RelationalExpression(
                                                new VariableExpression("a"),
                                                new VariableExpression("b"),
                                                RelationalExpression.RelationalOperation.EQUAL
                                        )
                                ),
                                new PrintStatement(
                                        new RelationalExpression(
                                                new VariableExpression("a"),
                                                new VariableExpression("b"),
                                                RelationalExpression.RelationalOperation.GREATER_THAN_OR_EQUAL
                                        )
                                )
                        )
                )
        );

        this.programsDescriptions.put("int a;int b;a=5;b=10; print(a!=b);print(a<b);print(a==b);print(a>=b)", ex8);


        IStatement ex9 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement("v", new ReferenceType(new IntType())),
                                new NewStatement("v", new ValueExpression(new IntValue(20)))
                        ),
                        new CompoundStatement(
                                new VariableDeclarationStatement(
                                        "a",
                                        new ReferenceType(
                                                new ReferenceType(
                                                        new IntType()
                                                )
                                        )
                                ),
                                new NewStatement("a", new VariableExpression("v"))
                        )
                ),
                new CompoundStatement(
                        new PrintStatement(new VariableExpression("v")),
                        new PrintStatement(new VariableExpression("a"))
                )
        );

        this.programsDescriptions.put("ref int v;new(v,20);Ref Ref int a; new(a,v);print(v);print(a)", ex9);

        IStatement ex10 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement("v", new ReferenceType(new IntType())),
                                new NewStatement("v", new ValueExpression(new IntValue(20)))
                        ),
                        new CompoundStatement(
                                new VariableDeclarationStatement(
                                        "a",
                                        new ReferenceType(
                                                new ReferenceType(
                                                        new StringType()
                                                )
                                        )
                                ),
                                new NewStatement("a", new VariableExpression("v"))
                        )
                ),
                new CompoundStatement(
                        new PrintStatement(new VariableExpression("v")),
                        new PrintStatement(new VariableExpression("a"))
                )
        );

        this.programsDescriptions.put("ref int v;new(v,20);Ref Ref string a; new(a,v);print(v);print(a)", ex10);

        IStatement ex11 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement("v",
                                        new ReferenceType(
                                                new IntType()
                                        )
                                ),
                                new NewStatement("v",
                                        new ValueExpression(
                                                new IntValue(20)
                                        )
                                )
                        ),
                        new CompoundStatement(
                                new VariableDeclarationStatement("a",
                                        new ReferenceType(
                                                new ReferenceType(
                                                        new IntType()
                                                )
                                        )
                                ),
                                new NewStatement("a",
                                        new VariableExpression("v")
                                )
                        )
                ),
                new CompoundStatement(
                        new PrintStatement(
                                new ReadHeapExpression(
                                        new VariableExpression("v")
                                )
                        ),
                        new PrintStatement(
                                new ArithmeticExpression(
                                        new ReadHeapExpression(
                                                new ReadHeapExpression(
                                                        new VariableExpression("a")
                                                )
                                        ),
                                        new ValueExpression(new IntValue(5)),
                                        ArithmeticExpression.ArithmeticOperation.ADDITION
                                )
                        )
                )
        );

        this.programsDescriptions.put("ref int v;new(v,20);Ref Ref int a; new(a,v);print(rH(v));print(rH(rH(a))+5)", ex11);

        IStatement ex12 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement(
                                        "v",
                                        new ReferenceType(
                                                new IntType()
                                        )
                                ),
                                new NewStatement(
                                        "v",
                                        new ValueExpression(
                                                new IntValue(20)
                                        )
                                )
                        ),
                        new CompoundStatement(
                                new PrintStatement(
                                        new ReadHeapExpression(
                                                new VariableExpression("v")
                                        )
                                ),
                                new WriteHeapStatement(
                                        "v",
                                        new ValueExpression(
                                                new IntValue(30)
                                        )
                                )
                        )
                ),
                new PrintStatement(
                        new ArithmeticExpression(
                                new ReadHeapExpression(
                                        new VariableExpression("v")
                                ),
                                new ValueExpression(
                                        new IntValue(5)
                                ),
                                ArithmeticExpression.ArithmeticOperation.ADDITION
                        )
                )
        );

        this.programsDescriptions.put("ref int v;new(v,20);print(rH(v)); wH(v,30);print(rH(v)+5);", ex12);

        IStatement ex13 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement(
                                        "v",
                                        new ReferenceType(
                                                new IntType()
                                        )
                                ),
                                new NewStatement(
                                        "v",
                                        new ValueExpression(
                                                new IntValue(20)
                                        )
                                )
                        ),
                        new CompoundStatement(
                                new VariableDeclarationStatement(
                                        "a",
                                        new ReferenceType(
                                                new ReferenceType(
                                                        new IntType()
                                                )
                                        )
                                ),
                                new NewStatement(
                                        "a",
                                        new VariableExpression("v")
                                )
                        )
                ),
                new CompoundStatement(
                        new NewStatement(
                                "v",
                                new ValueExpression(
                                        new IntValue(30)
                                )
                        ),
                        new PrintStatement(
                                new ReadHeapExpression(
                                        new ReadHeapExpression(
                                                new VariableExpression("a")
                                        )
                                )
                        )
                )
        );

        this.programsDescriptions.put("ref int v;new(v,20);ref ref int a; new(a,v); new(v,30);print(rH(rH(a)));", ex13);

        IStatement ex14 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement(
                                        "v",
                                        new ReferenceType(
                                                new IntType()
                                        )
                                ),
                                new NewStatement(
                                        "v",
                                        new ValueExpression(
                                                new IntValue(20)
                                        )
                                )
                        ),
                        new CompoundStatement(
                                new VariableDeclarationStatement(
                                        "a",
                                        new ReferenceType(
                                                new ReferenceType(
                                                        new IntType()
                                                )
                                        )
                                ),
                                new NewStatement(
                                        "a",
                                        new VariableExpression("v")
                                )
                        )
                ),
                new CompoundStatement(
                        new NewStatement(
                                "v",
                                new ValueExpression(
                                        new IntValue(30)
                                )
                        ),
                        new CompoundStatement(
                                new PrintStatement(
                                        new ReadHeapExpression(
                                                new ReadHeapExpression(
                                                        new VariableExpression("a")
                                                )
                                        )
                                ),
                                new CompoundStatement(
                                        new NewStatement(
                                                "a",
                                                new VariableExpression("v")
                                        ),
                                        new CompoundStatement(
                                                new PrintStatement(
                                                        new VariableExpression("a")
                                                ),
                                                new PrintStatement(
                                                        new ReadHeapExpression(
                                                                new VariableExpression("a")
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        this.programsDescriptions.put("ref int v;" +
                "new(v,20);" +
                "ref ref int a; " +
                "new(a,v); new(v,30);" +
                "print(rH(rH(a)));" +
                "new(a,v);" +
                "print(a); " +
                "print(rH(a))",
                ex14);

        IStatement ex15 = new CompoundStatement(
                new CompoundStatement(
                        new VariableDeclarationStatement(
                                "v",
                                new IntType()),
                        new AssignStatement(
                                "v",
                                new ValueExpression(
                                        new IntValue(4)
                                )
                        )
                ),
                new CompoundStatement(
                        new WhileStatement(
                                new RelationalExpression(
                                        new VariableExpression("v"),
                                        new ValueExpression(
                                                new IntValue(0)
                                        ),
                                        RelationalExpression.RelationalOperation.GREATER_THAN
                                ),
                                new CompoundStatement(
                                        new PrintStatement(
                                                new VariableExpression("v")
                                        ),
                                        new AssignStatement(
                                                "v",
                                                new ArithmeticExpression(
                                                        new VariableExpression("v"),
                                                        new ValueExpression(
                                                                new IntValue(1)
                                                        ),
                                                        ArithmeticExpression.ArithmeticOperation.SUBTRACTION
                                                )
                                        )
                                )
                        ),
                        new PrintStatement(
                                new VariableExpression("v")
                        )
                )
        );

        this.programsDescriptions.put("int v; v=4; (while (v>0) print(v);v=v-1);print(v)", ex15);

        IStatement ex16 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement(
                                        "v",
                                        new IntType()),
                                new VariableDeclarationStatement(
                                        "a",
                                        new ReferenceType(
                                                new IntType()
                                        )
                                )
                        ),
                        new CompoundStatement(
                                new AssignStatement(
                                        "v",
                                        new ValueExpression(
                                                new IntValue(10)
                                        )
                                ),
                                new NewStatement(
                                        "a",
                                        new ValueExpression(
                                                new IntValue(22)
                                        )
                                )
                        )
                ),
                new CompoundStatement(
                        new ForkStatement(
                                new CompoundStatement(
                                        new WriteHeapStatement(
                                                "a",
                                                new ValueExpression(
                                                        new IntValue(30)
                                                )
                                        ),
                                        new CompoundStatement(
                                                new AssignStatement(
                                                        "v",
                                                        new ValueExpression(
                                                                new IntValue(32)
                                                        )
                                                ),
                                                new CompoundStatement(
                                                        new PrintStatement(
                                                                new VariableExpression("v")
                                                        ),
                                                        new PrintStatement(
                                                                new ReadHeapExpression(
                                                                        new VariableExpression("a")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        ),
                        new CompoundStatement(
                                new PrintStatement(
                                        new VariableExpression("v")
                                ),
                                new PrintStatement(
                                        new ReadHeapExpression(
                                                new VariableExpression("a")
                                        )
                                )
                        )
                )
        );

        this.programsDescriptions.put(
                "int v;" +
                "ref int a;" +
                "v=10;" +
                "new(a,22);" +
                "fork(wH(a,30);" +
                "v=32;print(v);" +
                "print(rH(a)));" +
                "print(v);" +
                "print(rH(a));",
                ex16
        );

        IStatement ex17 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement(
                                        "v",
                                        new IntType()
                                ),
                                new VariableDeclarationStatement(
                                        "a",
                                        new ReferenceType(
                                                new IntType()
                                        )
                                )
                        ),
                        new AssignStatement(
                                "v",
                                new ValueExpression(
                                        new IntValue(1)
                                )
                        )
                ),
                new WhileStatement(
                        new RelationalExpression(
                                new VariableExpression("v"),
                                new ValueExpression(
                                        new IntValue(10)
                                ),
                                RelationalExpression.RelationalOperation.NOT_EQUAL
                        ),
                        new CompoundStatement(
                                new ForkStatement(
                                        new CompoundStatement(
                                                new PrintStatement(
                                                        new VariableExpression("v")
                                                ),
                                                new CompoundStatement(
                                                        new NewStatement(
                                                                "a",
                                                                new VariableExpression("v")
                                                        ),
                                                        new CompoundStatement(
                                                                new CompoundStatement(
                                                                        new AssignStatement(
                                                                                "v",
                                                                                new ArithmeticExpression(
                                                                                        new VariableExpression("v"),
                                                                                        new ValueExpression(
                                                                                                new IntValue(1)
                                                                                        ),
                                                                                        ArithmeticExpression.ArithmeticOperation.ADDITION
                                                                                )
                                                                        ),
                                                                        new AssignStatement(
                                                                                "v",
                                                                                new ArithmeticExpression(
                                                                                        new VariableExpression("v"),
                                                                                        new ValueExpression(
                                                                                                new IntValue(1)
                                                                                        ),
                                                                                        ArithmeticExpression.ArithmeticOperation.ADDITION
                                                                                )
                                                                        )
                                                                ),
                                                                new CompoundStatement(
                                                                        new AssignStatement(
                                                                                "v",
                                                                                new ArithmeticExpression(
                                                                                        new VariableExpression("v"),
                                                                                        new ValueExpression(
                                                                                                new IntValue(1)
                                                                                        ),
                                                                                        ArithmeticExpression.ArithmeticOperation.ADDITION
                                                                                )
                                                                        ),
                                                                        new AssignStatement(
                                                                                "v",
                                                                                new ArithmeticExpression(
                                                                                        new VariableExpression("v"),
                                                                                        new ValueExpression(
                                                                                                new IntValue(1)
                                                                                        ),
                                                                                        ArithmeticExpression.ArithmeticOperation.ADDITION
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                ),
                                new AssignStatement(
                                        "v",
                                        new ArithmeticExpression(
                                                new VariableExpression("v"),
                                                new ValueExpression(
                                                        new IntValue(1)
                                                ),
                                                ArithmeticExpression.ArithmeticOperation.ADDITION
                                        )
                                )
                        )
                )
        );

        this.programsDescriptions.put(
                "int v;" +
                "ref int a;" +
                "v=1;" +
                "while(v!=10){" +
                "fork(print(v);" +
                "new(a, v);" +
                "v=v+1;" +
                "v=v+1;" +
                "v=v+1;" +
                "v=v+1;" +
                ");" +
                "v=v+1;}",
                ex17
        );

        IStatement ex18 = new CompoundStatement(
                new CompoundStatement(
                        new VariableDeclarationStatement("a",
                                new BoolType()),
                        new CompoundStatement(
                                new AssignStatement("a",
                                        new ValueExpression(
                                                new BoolValue(true)
                                        )
                                ),
                                new IfStatement(
                                        new VariableExpression("a"),
                                        new VariableDeclarationStatement("b",
                                                new IntType()),
                                        new AssignStatement("a",
                                                new ValueExpression(
                                                        new BoolValue(false)
                                                ))
                                )
                        )
                ),
                new CompoundStatement(
                        new PrintStatement(
                                new VariableExpression("a")
                        ),
                        new PrintStatement(
                                new VariableExpression("b")
                        )
                )
        );

        this.programsDescriptions.put(
                "bool a;" +
                "a = true;" +
                "if(a){" +
                "int b;}" +
                "else{" +
                "a = false;}" +
                "print(a);" +
                "print(b);",
                ex18
        );

        IStatement ex19 = new CompoundStatement(
                new CompoundStatement(
                        new VariableDeclarationStatement(
                                "n",
                                new IntType()
                        ),
                        new AssignStatement(
                                "n",
                                new ValueExpression(
                                        new IntValue(5)
                                )
                        )
                ),
                new CompoundStatement(
                        new ForStatement(
                                new CompoundStatement(
                                        new VariableDeclarationStatement(
                                                "i",
                                                new IntType()
                                        ),
                                        new AssignStatement(
                                                "i",
                                                new ValueExpression(
                                                        new IntValue(1)
                                                )
                                        )
                                ),
                                new RelationalExpression(
                                        new VariableExpression("i"),
                                        new VariableExpression("n"),
                                        RelationalExpression.RelationalOperation.LESS_THAN_OR_EQUAL
                                ),
                                 new AssignStatement(
                                         "i",
                                         new ArithmeticExpression(
                                                 new VariableExpression("i"),
                                                 new ValueExpression(
                                                         new IntValue(1)
                                                 ),
                                                 ArithmeticExpression.ArithmeticOperation.ADDITION
                                         )
                                 ),
                                new CompoundStatement(
                                        new VariableDeclarationStatement(
                                                "v",
                                                new ReferenceType(
                                                        new IntType()
                                                )
                                        ),
                                        new CompoundStatement(
                                                new NewStatement(
                                                        "v",
                                                        new VariableExpression("i")
                                                ),
                                                new PrintStatement(
                                                        new ReadHeapExpression(
                                                                new VariableExpression("v")
                                                        )
                                                )
                                        )
                                )
                        ),
                        new PrintStatement(
                                new VariableExpression("i")
                        )
                )
        );

        this.programsDescriptions.put(
                "int n;" +
                "n=5;" +
                "for(int i=1;i<=n;i=i+1){" +
                    "ref int v;" +
                    "new(v, i);" +
                    "print(rH(v));" +
                "}" +
                "print(i);",
                ex19
        );

        IStatement ex20 = new CompoundStatement(
                new CompoundStatement(
                        new VariableDeclarationStatement(
                                "n",
                                new IntType()
                        ),
                        new AssignStatement(
                                "n",
                                new ValueExpression(
                                        new IntValue(5)
                                )
                        )
                ),
                new CompoundStatement(
                        new ForStatement(
                                new CompoundStatement(
                                        new VariableDeclarationStatement(
                                                "i",
                                                new IntType()
                                        ),
                                        new AssignStatement(
                                                "i",
                                                new ValueExpression(
                                                        new IntValue(1)
                                                )
                                        )
                                ),
                                new RelationalExpression(
                                        new VariableExpression("i"),
                                        new VariableExpression("n"),
                                        RelationalExpression.RelationalOperation.LESS_THAN_OR_EQUAL
                                ),
                                new AssignStatement(
                                        "i",
                                        new ArithmeticExpression(
                                                new VariableExpression("i"),
                                                new ValueExpression(
                                                        new IntValue(1)
                                                ),
                                                ArithmeticExpression.ArithmeticOperation.ADDITION
                                        )
                                ),
                                new CompoundStatement(
                                        new VariableDeclarationStatement(
                                                "v",
                                                new ReferenceType(
                                                        new IntType()
                                                )
                                        ),
                                        new CompoundStatement(
                                                new NewStatement(
                                                        "v",
                                                        new VariableExpression("i")
                                                ),
                                                new PrintStatement(
                                                        new ReadHeapExpression(
                                                                new VariableExpression("v")
                                                        )
                                                )
                                        )
                                )
                        ),
                        new PrintStatement(
                                new VariableExpression("v")
                        )
                )
        );

        this.programsDescriptions.put(
                "int n;" +
                "n=5;" +
                "for(int i=1;i<=n;i=i+1){" +
                "ref int v;" +
                "new(v, i);" +
                "print(rH(v));" +
                "}" +
                "print(v);",
                ex20
        );

        IStatement ex21 = new CompoundStatement(
                new CompoundStatement(
                        new VariableDeclarationStatement(
                                "n",
                                new IntType()
                        ),
                        new AssignStatement(
                                "n",
                                new ValueExpression(
                                        new IntValue(2)
                                )
                        )
                ),
                new SwitchStatement(
                        new VariableExpression("n"),
                         new Pair<>(
                                 new ValueExpression(new IntValue(1)),
                                 new PrintStatement(
                                         new ValueExpression(
                                                 new StringValue("case 1")
                                         )
                                 )
                         ),
                        new Pair<>(
                                new ValueExpression(new IntValue(2)),
                                new PrintStatement(
                                        new ValueExpression(
                                                new StringValue("case 2")
                                        )
                                )
                        ),
                        new Pair<>(
                                new ValueExpression(new IntValue(3)),
                                new PrintStatement(
                                        new ValueExpression(
                                                new StringValue("case 3")
                                        )
                                )
                        )
                )
        );

        this.programsDescriptions.put(
                "int n;" +
                "n=2;" +
                "switch(n){" +
                "case 1 -> print(\"case1\")" +
                "case 2 -> print(\"case2\")" +
                "case 3 -> print(\"case3\")" +
                "}",
                ex21
        );

        IStatement ex22 = new CompoundStatement(
            new CompoundStatement(
                    new CompoundStatement(
                            new VariableDeclarationStatement(
                                    "a",
                                    new ReferenceType(
                                            new IntType()
                                    )
                            ),
                            new CompoundStatement(
                                    new VariableDeclarationStatement(
                                        "b",
                                        new ReferenceType(
                                                new IntType()
                                        )
                                    ),
                                    new VariableDeclarationStatement(
                                            "v",
                                            new IntType()
                                    )
                            )
                    ),
                    new CompoundStatement(
                        new CompoundStatement(
                                new NewStatement(
                                        "a",
                                        new ValueExpression(
                                                new IntValue(0)
                                        )
                                ),
                                new NewStatement(
                                        "b",
                                        new ValueExpression(
                                                new IntValue(0)
                                        )
                                )
                        ),
                            new CompoundStatement(
                                    new WriteHeapStatement(
                                            "a",
                                            new ValueExpression(
                                                    new IntValue(1)
                                            )
                                    ),
                                    new WriteHeapStatement(
                                            "b",
                                            new ValueExpression(
                                                    new IntValue(2)
                                            )
                                    )
                            )
                    )
            ),
            new CompoundStatement(
                new CompoundStatement(
                        new ConditionalAssignmentStatement(
                                "v",
                                new RelationalExpression(
                                        new ReadHeapExpression(
                                                new VariableExpression("a")
                                        ),
                                        new ReadHeapExpression(
                                                new VariableExpression("b")
                                        ),
                                        RelationalExpression.RelationalOperation.LESS_THAN
                                ),
                                new ValueExpression(new IntValue(100)),
                                new ValueExpression(new IntValue(200))
                        ),
                        new PrintStatement(new VariableExpression("v"))
                ),
                    new CompoundStatement(
                            new ConditionalAssignmentStatement(
                                    "v",
                                    new RelationalExpression(
                                            new ArithmeticExpression(
                                                    new ReadHeapExpression(
                                                            new VariableExpression("b")
                                                    ),
                                                    new ValueExpression(new IntValue(2)),
                                                    ArithmeticExpression.ArithmeticOperation.SUBTRACTION
                                            ),
                                            new ReadHeapExpression(
                                                    new VariableExpression("a")
                                            ),
                                            RelationalExpression.RelationalOperation.GREATER_THAN
                                    ),
                                    new ValueExpression(new IntValue(100)),
                                    new ValueExpression(new IntValue(200))
                            ),
                            new PrintStatement(new VariableExpression("v"))
                    )
            )
        );
        this.programsDescriptions.put(
                "Ref int a; Ref int b; int v;\n" +
                "new(a,0); new(b,0);\n" +
                "wh(a,1); wh(b,2);\n" +
                "v=(rh(a)<rh(b))?100:200;\n" +
                "print(v);\n" +
                "v= ((rh(b)-2)>rh(a))?100:200;\n" +
                "print(v);",
                ex22
        );


        IStatement ex23 = new CompoundStatement(
                new CompoundStatement(
                        new CompoundStatement(
                                new VariableDeclarationStatement(
                                        "a",
                                        new ReferenceType(
                                                new IntType()
                                        )
                                ),
                                new CompoundStatement(
                                        new VariableDeclarationStatement(
                                                "b",
                                                new ReferenceType(
                                                        new IntType()
                                                )
                                        ),
                                        new VariableDeclarationStatement(
                                                "v",
                                                new IntType()
                                        )
                                )
                        ),
                        new CompoundStatement(
                                new CompoundStatement(
                                        new NewStatement(
                                                "a",
                                                new ValueExpression(
                                                        new IntValue(0)
                                                )
                                        ),
                                        new NewStatement(
                                                "b",
                                                new ValueExpression(
                                                        new IntValue(0)
                                                )
                                        )
                                ),
                                new CompoundStatement(
                                        new WriteHeapStatement(
                                                "a",
                                                new ValueExpression(
                                                        new IntValue(1)
                                                )
                                        ),
                                        new WriteHeapStatement(
                                                "b",
                                                new ValueExpression(
                                                        new IntValue(2)
                                                )
                                        )
                                )
                        )
                ),
                new CompoundStatement(
                        new CompoundStatement(
                                new ConditionalAssignmentStatement(
                                        "a",
                                        new RelationalExpression(
                                                new ReadHeapExpression(
                                                        new VariableExpression("a")
                                                ),
                                                new ReadHeapExpression(
                                                        new VariableExpression("b")
                                                ),
                                                RelationalExpression.RelationalOperation.LESS_THAN
                                        ),
                                        new ValueExpression(new IntValue(100)),
                                        new ValueExpression(new IntValue(200))
                                ),
                                new PrintStatement(new VariableExpression("v"))
                        ),
                        new CompoundStatement(
                                new ConditionalAssignmentStatement(
                                        "v",
                                        new RelationalExpression(
                                                new ArithmeticExpression(
                                                        new ReadHeapExpression(
                                                                new VariableExpression("b")
                                                        ),
                                                        new ValueExpression(new IntValue(2)),
                                                        ArithmeticExpression.ArithmeticOperation.SUBTRACTION
                                                ),
                                                new ReadHeapExpression(
                                                        new VariableExpression("a")
                                                ),
                                                RelationalExpression.RelationalOperation.GREATER_THAN
                                        ),
                                        new ValueExpression(new IntValue(100)),
                                        new ValueExpression(new IntValue(200))
                                ),
                                new PrintStatement(new VariableExpression("v"))
                        )
                )
        );

        this.programsDescriptions.put(
                "Ref int a; Ref int b; int v;\n" +
                "new(a,0); new(b,0);\n" +
                "wh(a,1); wh(b,2);\n" +
                "a=(rh(a)<rh(b))?100:200;\n" +
                "print(v);\n" +
                "v= ((rh(b)-2)>rh(a))?100:200;\n" +
                "print(v);",
                ex23
        );

        IStatement ex24 = new CompoundStatement(
            new CompoundStatement(
                    new CompoundStatement(
                            new VariableDeclarationStatement("v", new IntType()),
                            new VariableDeclarationStatement("x", new IntType())
                    ),
                    new CompoundStatement(
                            new VariableDeclarationStatement("y", new IntType()),
                            new AssignStatement("v", new ValueExpression(new IntValue(0)))
                    )),
                    new CompoundStatement(
                            new RepeatUntilStatement(
                                    new CompoundStatement(
                                            new ForkStatement(
                                                    new CompoundStatement(
                                                            new PrintStatement(new VariableExpression("v")),
                                                            new AssignStatement(
                                                                    "v",
                                                                    new ArithmeticExpression(
                                                                            new VariableExpression("v"),
                                                                            new ValueExpression(new IntValue(1)),
                                                                            ArithmeticExpression.ArithmeticOperation.SUBTRACTION
                                                                    )
                                                            )
                                                    )
                                            ),
                                            new AssignStatement(
                                                    "v",
                                                    new ArithmeticExpression(
                                                            new VariableExpression("v"),
                                                            new ValueExpression(new IntValue(1)),
                                                            ArithmeticExpression.ArithmeticOperation.ADDITION
                                                    )
                                            )
                                    ),
                                    new RelationalExpression(
                                            new VariableExpression("v"),
                                            new ValueExpression(new IntValue(3)),
                                            RelationalExpression.RelationalOperation.EQUAL
                                    )

                            ),
                            new CompoundStatement(
                                    new AssignStatement(
                                            "x",
                                            new ValueExpression(new IntValue(1))
                                    ),
                                    new CompoundStatement(
                                            new NopStatement(),
                                            new CompoundStatement(
                                                    new AssignStatement(
                                                            "y",
                                                            new ValueExpression(new IntValue(3))
                                                    ),
                                                    new CompoundStatement(
                                                            new NopStatement(),
                                                            new PrintStatement(
                                                                    new ArithmeticExpression(
                                                                            new VariableExpression("v"),
                                                                            new ValueExpression(new IntValue(10)),
                                                                            ArithmeticExpression.ArithmeticOperation.MULTIPLICATION
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );

        this.programsDescriptions.put(
                "int v; int x; int y; v=0;\n" +
                "(repeat (fork(print(v);v=v-1);v=v+1) until v==3);\n" +
                "x=1;nop;y=3;nop;\n" +
                "print(v*10)      ",
                ex24
        );

        IStatement ex25 = new CompoundStatement(
            new VariableDeclarationStatement("v", new IntType()),
            new CompoundStatement(
                new AssignStatement("v", new ValueExpression(new IntValue(0))),
                new CompoundStatement(
                        new WhileStatement(
                                new RelationalExpression(
                                        new VariableExpression("v"),
                                        new ValueExpression(new IntValue(3)),
                                        RelationalExpression.RelationalOperation.LESS_THAN
                                ),
                                new CompoundStatement(
                                        new ForkStatement(
                                                new CompoundStatement(
                                                        new PrintStatement(new VariableExpression("v")),
                                                        new AssignStatement(
                                                                "v",
                                                                new ArithmeticExpression(
                                                                        new VariableExpression("v"),
                                                                        new ValueExpression(new IntValue(1)),
                                                                        ArithmeticExpression.ArithmeticOperation.SUBTRACTION
                                                                )
                                                        )
                                                )
                                        ),
                                        new AssignStatement(
                                                "v",
                                                new ArithmeticExpression(
                                                        new VariableExpression("v"),
                                                        new ValueExpression(new IntValue(1)),
                                                        ArithmeticExpression.ArithmeticOperation.ADDITION
                                                )
                                        )
                                )
                        ),
                        new CompoundStatement(
                                new SleepStatement(new ValueExpression(new IntValue(5))),
                                new PrintStatement(
                                        new ArithmeticExpression(
                                                new VariableExpression("v"),
                                                new ValueExpression(new IntValue(10)),
                                                ArithmeticExpression.ArithmeticOperation.MULTIPLICATION
                                        )
                                )
                        )
                )
            )
        );

        this.programsDescriptions.put(
            "v=0;\n" +
                "(while(v<3) (fork(print(v);v=v+1);v=v+1);\n" +
                "sleep(5);\n" +
                "print(v*10)"
            ,ex25
        );

        IStatement ex26 = new CompoundStatement(
                new CompoundStatement(
                    new VariableDeclarationStatement("v1", new IntType()),
                    new VariableDeclarationStatement("v2", new IntType())
                ),
                new CompoundStatement(
                        new CompoundStatement(
                                new AssignStatement("v1", new ValueExpression(new IntValue(2))),
                                new AssignStatement("v2", new ValueExpression(new IntValue(3)))
                        ),
                        new PrintStatement(
                                new MulExpression(
                                        new VariableExpression("v1"),
                                        new VariableExpression("v2")
                                )
                        )
                )
        );

        this.programsDescriptions.put(
                "v1=2;v2=3; print(MUL(v1,v2));",
                ex26
        );
    }
}

package View;

import Controller.Controller;
import Model.ADTs.*;
import Model.Expressions.*;
import Model.ProgramState;
import Model.Statements.*;
import Model.Types.BoolType;
import Model.Types.IntType;
import Model.Types.StringType;
import Model.Values.BoolValue;
import Model.Values.IValue;
import Model.Values.IntValue;
import Model.Values.StringValue;
import Repository.IRepository;
import Repository.Repository;

import java.io.BufferedReader;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class View {
    TextMenu menu;
    LinkedHashMap<IStatement, String> programsDescriptions;

    public View(){
        this.menu = new TextMenu();
        this.programsDescriptions = new LinkedHashMap<>();
        this.setupPrograms();
    }

    public void start(){
        this.menu.show();
    }

    void setupPrograms(){
        IStatement ex1 = new CompoundStatement(
                new VariableDeclarationStatement("v", new IntType()),
                new CompoundStatement(
                        new AssignStatement("v", new ValueExpression(new IntValue(2))),
                        new PrintStatement(new VariableExpression("v"))
                )
        );
        this.programsDescriptions.put(ex1, "int v; v=2; Print(v)");

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
        this.programsDescriptions.put(ex2, "int a; int b; a=2+3*5; b=a+1; Print(b)");


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

        this.programsDescriptions.put(ex3, "bool a; int v; a=true; (If a Then v=2 Else v=3); Print(v)");

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
        this.programsDescriptions.put(ex4, "int a; a=10/0; Print(a)");

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
        this.programsDescriptions.put(ex5, "int a; a=10/2; Print(b)");

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
        this.programsDescriptions.put(ex6, "bool a; a = true; bool b; Print(a && b); Print(a || b)");

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
                                new CloseReadFile(new ValueExpression(new StringValue("src/Files/test.in")))
                        )
                )
        );
        this.programsDescriptions.put(ex7, "string varf;varf=\"test.in\";openRFile(varf);int varc; readFile(varf,varc);print(varc); readFile(varf,varc);print(varc);closeRFile(varf)");

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

        this.programsDescriptions.put(ex8, "int a;int b;a=5;b=10; print(a!=b);print(a<b);print(a==b);print(a>=b)");


        AtomicInteger currentKey = new AtomicInteger(1);
        this.programsDescriptions.forEach(
                (statement, description) ->{
                    MyIStack<IStatement> executionStack = new MyStack<>();
                    executionStack.push(statement);
                    MyIDictionary<String, IValue> symbolsTable = new MyDictionary<>();
                    MyIList<IValue> out = new MyList<>();
                    MyIDictionary<String, BufferedReader> fileTable = new MyDictionary<>();

                    ProgramState newProgram = new ProgramState(executionStack, symbolsTable, out, fileTable, statement);

                    IRepository repository = new Repository("src/Files/log" + currentKey + ".txt");
                    Controller controller = new Controller(repository);
                    controller.addProgramState(newProgram);

                    this.menu.addCommand(new RunExample(String.valueOf(currentKey.get()), description, controller));
                    currentKey.getAndIncrement();
                }
        );

        this.menu.addCommand(new ExitCommand("x", "exit"));
        }
    }


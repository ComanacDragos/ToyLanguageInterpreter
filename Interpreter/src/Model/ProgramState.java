package Model;

import Exceptions.EmptyCollection;
import Exceptions.MyException;
import Model.ADTs.*;
import Model.Statements.IStatement;
import Model.Values.IValue;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ProgramState {
    MyIStack<IStatement> executionStack;
    MyIStack<MyIDictionary<String, IValue>> symbolsTable;
    MyIList<IValue> out;
    IStatement originalProgram;
    MyIDictionary<String, BufferedReader> fileTable;
    MyHeap heap;
    Integer programId;
    static AtomicInteger currentId = new AtomicInteger(0);

    MyIDictionary<String, Pair<List<String>, IStatement>> proceduresTable;

    public ProgramState(MyIStack<IStatement> executionStack, MyIStack<MyIDictionary<String, IValue>> symbolsTable, MyIList<IValue> out, MyIDictionary<String, BufferedReader> fileTable, MyHeap heap, MyIDictionary<String, Pair<List<String>, IStatement>> proceduresTable, IStatement originalProgram){
        this.executionStack = executionStack;
        this.symbolsTable = symbolsTable;
        this.out = out;
        this.originalProgram = originalProgram;
        this.fileTable = fileTable;
        this.heap = heap;
        this.programId = ProgramState.currentId.incrementAndGet();

        this.proceduresTable = proceduresTable;
    }

    public IStatement getOriginalProgram() {
        return originalProgram;
    }

    public MyIDictionary<String, IValue> getSymbolsTable() {
        return symbolsTable.peek();
    }

    public MyIList<IValue> getOut() {
        return out;
    }

    public MyIStack<IStatement> getExecutionStack() {
        return executionStack;
    }

    public void setExecutionStack(MyIStack<IStatement> executionStack) {
        this.executionStack = executionStack;
    }

    public void setOriginalProgram(IStatement originalProgram) {
        this.originalProgram = originalProgram;
    }

    public void setOut(MyIList<IValue> out) {
        this.out = out;
    }

    public void setSymbolsTable(MyIStack<MyIDictionary<String, IValue>> symbolsTable) {
        this.symbolsTable = symbolsTable;
    }

    public MyIDictionary<String, Pair<List<String>, IStatement>> getProceduresTable() {
        return proceduresTable;
    }

    public void setProceduresTable(MyIDictionary<String, Pair<List<String>, IStatement>> proceduresTable) {
        this.proceduresTable = proceduresTable;
    }

    public MyIStack<MyIDictionary<String, IValue>> getSymbolsTableStack(){
        return this.symbolsTable;
    }

    public MyIDictionary<String, BufferedReader> getFileTable() {
        return fileTable;
    }

    public void setFileTable(MyIDictionary<String, BufferedReader> fileTable) {
        this.fileTable = fileTable;
    }

    public MyHeap getHeap() {
        return heap;
    }

    public void setHeap(MyHeap heap) {
        this.heap = heap;
    }

    public Integer getProgramId() {
        return programId;
    }

    public MyIStack<IStatement> executionStackDeepCopy(){
        MyIStack<IStatement> newExecutionStack = new MyStack<>();
        this.executionStack.stream().map(
                        IStatement::deepCopy
                ).forEach(newExecutionStack::push);
        return newExecutionStack;
    }

    public MyIStack<MyIDictionary<String, IValue>> symbolsTableDeepCopy(){
        MyIStack<MyIDictionary<String, IValue>> auxSymbolsTableStack = new MyStack<>();

        this.symbolsTable.stream()
                .forEach(
                        table->{
                            MyIDictionary<String, IValue> newSymbolsTable = new MyDictionary<>();
                            table.stream().collect(
                                    Collectors.toMap(Map.Entry::getKey, e -> e.getValue().deepCopy())
                            ).entrySet().stream().forEach(
                                    e -> newSymbolsTable.put(e.getKey(), e.getValue())
                            );
                            auxSymbolsTableStack.push(table);
                        }
                );
        MyIStack<MyIDictionary<String, IValue>> newSymbolsTableStack = new MyStack<>();
        auxSymbolsTableStack.forEach(newSymbolsTableStack::push);
        return newSymbolsTableStack;
    }

    public MyIList<IValue> outDeepCopy(){
        MyIList<IValue> newOut = new MyList<>();

        this.out.stream().map(
                IValue::deepCopy
        ).forEach(newOut::add);
        return newOut;
    }

    public MyIDictionary<String, BufferedReader> fileTableDeepCopy(){
        MyIDictionary<String, BufferedReader> newFileTable = new MyDictionary<>();
        this.fileTable.stream().forEach(
                e -> newFileTable.put(e.getKey(), e.getValue())
        );
        return newFileTable;
    }

    public MyHeap heapDeepCopy(){
        MyHeap newHeap = new MyHeap();

        this.heap.stream().forEach(
                e -> newHeap.put(e.getKey(), e.getValue().deepCopy())
        );
        return newHeap;
    }

    public MyIDictionary<String, Pair<List<String>, IStatement>> proceduresTableDeepCopy(){
        MyIDictionary<String, Pair<List<String>, IStatement>> newProceduresTable = new MyDictionary<>();

        this.proceduresTable.stream().forEach(
                e->newProceduresTable.put(e.getKey(), new Pair<>(e.getValue().getKey(), e.getValue().getValue()))
        );

        return newProceduresTable;
    }
    public ProgramState deepCopy(){

        return new ProgramState(this.executionStackDeepCopy(),
                                this.symbolsTableDeepCopy(),
                                this.outDeepCopy(),
                                this.fileTableDeepCopy(),
                                this.heapDeepCopy(),
                                this.proceduresTableDeepCopy(),
                                this.originalProgram.deepCopy());
    }

    public Boolean isNotCompleted(){
        return !this.executionStack.isEmpty();
    }

    public ProgramState executeOneStep() throws MyException {
        if(this.executionStack.isEmpty())
            throw new EmptyCollection("Empty execution stack");
        return this.executionStack.pop().execute(this);
    }

    @Override
    public String toString() {
        return  "Program: " + this.programId + '\n' +
                "Execution stack\n" +this.executionStack.toString() +
                "Symbols table\n" + this.symbolsTable.toString() +
                "Out\n" + this.out.toString() +
                "File table\n" + this.fileTable.stream().map(Map.Entry::getKey).collect(Collectors.joining("\n")) +
                "Heap\n" + this.heap.toString() +
                "Procedures\n" + this.proceduresTable.toString()
                +"\n\n";
    }

    public static class ProceduresTableEntry{
        String name;
        String parameters;
        IStatement body;

        public ProceduresTableEntry(String name, List<String> parameters, IStatement body) {
            this.name = name;
            parameters.stream().reduce((a,b)->a + ", " + b).ifPresent(s->this.parameters = s);
            this.body = body;
        }

        public String getName() {
            return name;
        }

        public String getParameters() {
            return parameters;
        }

        public IStatement getBody() {
            return body;
        }

        public void setName(String variableName) {
            this.name = variableName;
        }

        public void setParameters(String parameters) {
            this.parameters = parameters;
        }

        public void setBody(IStatement body) {
            this.body = body;
        }
    }
}



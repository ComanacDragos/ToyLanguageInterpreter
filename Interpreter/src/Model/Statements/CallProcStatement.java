package Model.Statements;

import Exceptions.MyException;
import Model.ADTs.MyDictionary;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.ProgramState;
import Model.Types.IType;
import Model.Values.IValue;
import com.sun.javafx.tk.DummyToolkit;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CallProcStatement implements IStatement{
    String procedureName;
    List<IExpression> expressionList;

    public CallProcStatement(String procedureName, List<IExpression> expressionList) {
        this.procedureName = procedureName;
        this.expressionList = expressionList;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public List<IExpression> getExpressionList() {
        return expressionList;
    }

    public void setExpressionList(List<IExpression> expressionList) {
        this.expressionList = expressionList;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        if(state.getProceduresTable().isDefined(this.procedureName)){
            Pair<List<String>, IStatement> proc = state.getProceduresTable().lookup(this.procedureName);

            MyIDictionary<String, IValue> newSymbolsTable = new MyDictionary<>();

            ArrayList<IValue> values = new ArrayList<>();

            this.expressionList.forEach(
                    e->values.add(e.eval(state.getSymbolsTable(), state.getHeap()))
            );

            AtomicInteger index = new AtomicInteger(0);

            proc.getKey().forEach(
                    p->newSymbolsTable.put(p, values.get(index.getAndIncrement()))
            );

            state.getSymbolsTableStack().push(newSymbolsTable);
            state.getExecutionStack().push(new ReturnStatement());
            state.getExecutionStack().push(proc.getValue());
        }else{
            throw new MyException(this.procedureName + " does not exist");
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        List<IExpression> newList = new LinkedList<>();
        this.expressionList.forEach(e-> newList.add(e.deepCopy()));
        return new CallProcStatement(this.procedureName, newList);
    }

    @Override
    public String toString() {
        return this.procedureName + "(" + this.expressionList.stream().map(Object::toString).collect(Collectors.joining(", ")) + ");";
    }
}

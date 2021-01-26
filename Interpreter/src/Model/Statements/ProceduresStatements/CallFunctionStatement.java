package Model.Statements.ProceduresStatements;

import Exceptions.MyException;
import Model.ADTs.MyDictionary;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.IType;
import Model.Values.IValue;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CallFunctionStatement implements IStatement {
    String procedureName;
    List<IExpression> expressions;

    public CallFunctionStatement(String procedureName, List<IExpression> expressions) {
        this.procedureName = procedureName;
        this.expressions = expressions;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public List<IExpression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<IExpression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        if(state.getProceduresTable().isDefined(this.procedureName)){
            Pair<List<String>, IStatement> procedure = state.getProceduresTable().lookup(this.procedureName);
            MyIDictionary<String, IValue> procedureSymbolsTable = new MyDictionary<>();
            ArrayList<IValue> values = new ArrayList<>();

            this.expressions.forEach(
                    e->values.add(e.eval(state.getSymbolsTable(), state.getHeap()))
            );

            AtomicInteger index = new AtomicInteger(0);

            procedure.getKey().forEach(
                    s-> {
                        procedureSymbolsTable.put(s, values.get(index.getAndIncrement()));
                    }
            );

            state.getSymbolsTableStack().push(procedureSymbolsTable);
            state.getExecutionStack().push(new ReturnStatement());
            state.getExecutionStack().push(procedure.getValue());

        }else{
            throw new MyException(this.procedureName + " procedure does not exist");
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        List<IExpression> newExpressions = new LinkedList<>();

        this.expressions.forEach(
                e->newExpressions.add(e.deepCopy())
        );


        return new CallFunctionStatement(
                this.procedureName,
                newExpressions
        );
    }

    @Override
    public String toString() {
        return "call " + this.procedureName + "(" +
                this.expressions.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", "))+ ");";
    }
}

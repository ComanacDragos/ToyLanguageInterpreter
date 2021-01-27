package Model.Statements;

import Exceptions.MyException;
import Model.ADTs.MyIDictionary;
import Model.ProgramState;
import Model.Types.IType;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateProcedureStatement implements IStatement{
    String procedureName;
    List<String> parameters;
    IStatement statement;

    public CreateProcedureStatement(String procedureName, List<String> parameters, IStatement statement) {
        this.procedureName = procedureName;
        this.parameters = parameters;
        this.statement = statement;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public IStatement getStatement() {
        return statement;
    }

    public void setStatement(IStatement statement) {
        this.statement = statement;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        if(!state.getProceduresTable().isDefined(this.procedureName)){
            state.getProceduresTable().put(this.procedureName, new Pair<>(this.parameters, this.statement));
        }else{
            throw new MyException(this.procedureName + " procedure already exists");
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        List<String> newList = new LinkedList<>(this.parameters);
        return new CreateProcedureStatement(this.procedureName, newList, this.statement.deepCopy());
    }

    @Override
    public String toString() {
        return "create proc " + this.procedureName + "(" + String.join(", ", this.parameters) + "){\n" + this.statement + "\n}";
    }
}

package Model.Statements.ProceduresStatements;

import Exceptions.MyException;
import Model.ADTs.MyIDictionary;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.IType;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CreateProcedureStatement implements IStatement {
    String procedureName;
    List<String> parameters;
    IStatement procedureBody;

    public CreateProcedureStatement(String procedureName, List<String> parameters, IStatement procedureBody) {
        this.procedureName = procedureName;
        this.parameters = parameters;
        this.procedureBody = procedureBody;
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

    public IStatement getProcedureBody() {
        return procedureBody;
    }

    public void setProcedureBody(IStatement procedureBody) {
        this.procedureBody = procedureBody;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        if(state.getProceduresTable().isDefined(this.procedureName)){
            throw new MyException(this.procedureName + " procedure already defined");
        }
        state.getProceduresTable().put(this.procedureName, new Pair<>(this.parameters, this.procedureBody));
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        List<String> newParametersList = new LinkedList<>(this.parameters);
        return new CreateProcedureStatement(this.procedureName, newParametersList, this.procedureBody.deepCopy());
    }

    @Override
    public String toString() {
        return "procedure " + this.procedureName + "(" +
                String.join(", ", this.parameters) + ")\n{" + this.procedureBody + "\n}";
    }
}

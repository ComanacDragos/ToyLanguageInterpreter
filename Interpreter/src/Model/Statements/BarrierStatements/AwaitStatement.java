package Model.Statements.BarrierStatements;

import Exceptions.MyException;
import Exceptions.VariableNotDefined;
import Model.ADTs.MyIDictionary;
import Model.ProgramState;
import Model.Statements.AssignStatement;
import Model.Statements.IStatement;
import Model.Types.IType;
import Model.Values.IValue;
import Model.Values.IntValue;

public class AwaitStatement implements IStatement {
    String variableName;

    public AwaitStatement(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        if(state.getSymbolsTable().isDefined(this.variableName)){
            IValue value = state.getSymbolsTable().lookup(this.variableName);
            if(state.getBarrierTable().await(((IntValue)value).getValue(), state.getProgramId())){
                state.getExecutionStack().push(this);
            }
        }else{
            throw new VariableNotDefined(this.variableName);
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new AwaitStatement(this.variableName);
    }

    @Override
    public String toString() {
        return "await(" + this.variableName + ");";
    }
}

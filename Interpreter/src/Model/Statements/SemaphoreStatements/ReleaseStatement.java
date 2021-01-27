package Model.Statements.SemaphoreStatements;

import Exceptions.MyException;
import Exceptions.VariableNotDefined;
import Model.ADTs.MyIDictionary;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.IType;
import Model.Types.IntType;
import Model.Values.IValue;
import Model.Values.IntValue;

public class ReleaseStatement implements IStatement {
    String variableName;

    public ReleaseStatement(String variableName) {
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

            if(value.getType().equals(new IntType())){
                state.getSemaphoreTable().release(
                    ((IntValue)value).getValue(),
                    state.getProgramId()
                );
            }else{
                throw new MyException(this.variableName + " must be an int");
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
        return new ReleaseStatement(this.variableName);
    }

    @Override
    public String toString() {
        return "release(" + this.variableName + ");";
    }
}

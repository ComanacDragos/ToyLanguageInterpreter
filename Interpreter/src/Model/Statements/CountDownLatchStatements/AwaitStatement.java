package Model.Statements.CountDownLatchStatements;

import Exceptions.MyException;
import Exceptions.VariableNotDefined;
import Model.ADTs.MyIDictionary;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.IType;
import Model.Types.IntType;
import Model.Values.IValue;
import Model.Values.IntValue;
import javafx.util.Pair;

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
            if(value.getType().equals(new IntType())){
                IntValue intValue = (IntValue) value;
                if(state.getCountDownLatchTable().isDefined(intValue.getValue())){
                    if(state.getCountDownLatchTable().lookup(intValue.getValue()) != 0){
                        state.getExecutionStack().push(this);
                    }
                }else{
                    throw new MyException("Latch not defined");
                }
            }else{
                throw new MyException(this.variableName + " must be of type int");
            }
        }else{
            throw new VariableNotDefined(this.variableName);
        }

        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        if(!typeEnvironment.lookup(this.variableName).equals(new IntType())){
            throw new MyException(this.variableName + " must be of type int");
        }
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

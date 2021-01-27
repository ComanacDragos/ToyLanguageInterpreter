package Model.Statements.BarrierStatements;

import Exceptions.MyException;
import Exceptions.VariableNotDefined;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.IType;
import Model.Types.IntType;
import Model.Values.IValue;
import Model.Values.IntValue;

public class CreateBarrierStatement implements IStatement {
    String variableName;
    IExpression expression;

    public CreateBarrierStatement(String variableName, IExpression expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public IExpression getExpression() {
        return expression;
    }

    public void setExpression(IExpression expression) {
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IValue value = this.expression.eval(state.getSymbolsTable(), state.getHeap());
        if(value.getType().equals(new IntType())){
            if(state.getSymbolsTable().isDefined(this.variableName)){
                state.getSymbolsTable().update(
                        this.variableName,
                        new IntValue(state.getBarrierTable().put(((IntValue)value).getValue()))
                );
            }else{
                state.getSymbolsTable().put(
                        this.variableName,
                        new IntValue(state.getBarrierTable().put(((IntValue)value).getValue()))
                );
            }
        }else{
            throw new MyException(this.expression + " must be an integer");
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new CreateBarrierStatement(this.variableName, this.expression.deepCopy());
    }

    @Override
    public String toString() {
        return "newBarrier(" + this.variableName + ", " + this.expression + ");";
    }
}

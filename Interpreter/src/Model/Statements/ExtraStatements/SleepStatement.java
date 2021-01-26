package Model.Statements.ExtraStatements;

import Exceptions.MyException;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.Expressions.ValueExpression;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.IType;
import Model.Types.IntType;
import Model.Values.IValue;
import Model.Values.IntValue;

public class SleepStatement implements IStatement {
    IExpression expression;

    public SleepStatement(IExpression expression) {
        this.expression = expression;
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
            IntValue intValue = (IntValue)value;
            if(intValue.getValue() >0){
                this.setExpression(new ValueExpression(new IntValue(intValue.getValue()-1)));
                state.getExecutionStack().push(this);
            }
        }

        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        if(this.expression.typeCheck(typeEnvironment).equals(new IntType()))
            return typeEnvironment;
        throw new MyException(this.expression + " must be an int");
    }

    @Override
    public IStatement deepCopy() {
        return new SleepStatement(this.expression.deepCopy());
    }

    @Override
    public String toString() {
        return "sleep(" + this.expression + ");";
    }
}

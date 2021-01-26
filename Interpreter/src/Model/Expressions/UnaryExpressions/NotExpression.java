package Model.Expressions.UnaryExpressions;

import Exceptions.MyException;
import Model.ADTs.MyHeap;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.Types.BoolType;
import Model.Types.IType;
import Model.Values.BoolValue;
import Model.Values.IValue;

public class NotExpression implements IExpression {
    IExpression expression;

    public NotExpression(IExpression expression) {
        this.expression = expression;
    }

    public IExpression getExpression() {
        return expression;
    }

    public void setExpression(IExpression expression) {
        this.expression = expression;
    }

    @Override
    public IValue eval(MyIDictionary<String, IValue> symbolsTable, MyHeap heap) throws MyException {
        return new BoolValue(!((BoolValue)this.expression.eval(symbolsTable, heap)).getValue());
    }

    @Override
    public IType typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        if(expression.typeCheck(typeEnvironment).equals(new BoolType()))
            return new BoolType();
        throw new MyException(this.expression + " must be a boolean");
    }

    @Override
    public IExpression deepCopy() {
        return null;
    }

    @Override
    public String toString() {
        return "!(" + this.expression + ")";
    }
}

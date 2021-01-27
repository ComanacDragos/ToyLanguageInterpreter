package Model.Expressions.BinaryExpressions;

import Exceptions.DivisionByZero;
import Exceptions.MyException;
import Model.ADTs.MyHeap;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.Types.IType;
import Model.Types.IntType;
import Model.Values.IValue;
import Model.Values.IntValue;

public class MulExpression extends BinaryExpression {
    public MulExpression(IExpression leftSide, IExpression rightSide) {
        super(leftSide, rightSide);
    }

    @Override
    public IValue eval(MyIDictionary<String, IValue> symbolsTable, MyHeap heap) throws MyException, DivisionByZero {
        IntValue leftValue = (IntValue) this.leftSide.eval(symbolsTable, heap);
        IntValue rightValue = (IntValue) this.rightSide.eval(symbolsTable, heap);

        return new IntValue((leftValue.getValue()*rightValue.getValue()) - (leftValue.getValue()+rightValue.getValue()));
    }

    @Override
    public IType typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        IType type = this.leftSide.typeCheck(typeEnvironment);

        if(type.equals(new IntType())){
            type = this.rightSide.typeCheck(typeEnvironment);
            if(!type.equals(new IntType())){
                throw new MyException(this.rightSide + " must be an integer");
            }
        }else{
            throw new MyException(this.leftSide + " must be an integer");
        }

        return new IntType();
    }

    @Override
    public IExpression deepCopy() {
        return new MulExpression(this.leftSide.deepCopy(), this.rightSide.deepCopy());
    }

    @Override
    public String toString() {
        return "((" + this.leftSide + " * " + this.rightSide + ")-(" + this.leftSide + " + " + this.rightSide + "))";
    }
}

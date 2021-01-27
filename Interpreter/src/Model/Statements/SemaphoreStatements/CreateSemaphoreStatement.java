package Model.Statements.SemaphoreStatements;

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

import java.util.LinkedList;

public class CreateSemaphoreStatement implements IStatement {
    String variableName;
    IExpression expression1, expression2;

    public CreateSemaphoreStatement(String variableName, IExpression expression1, IExpression expression2) {
        this.variableName = variableName;
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public IExpression getExpression1() {
        return expression1;
    }

    public void setExpression1(IExpression expression1) {
        this.expression1 = expression1;
    }

    public IExpression getExpression2() {
        return expression2;
    }

    public void setExpression2(IExpression expression2) {
        this.expression2 = expression2;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IValue value1 = this.expression1.eval(state.getSymbolsTable(), state.getHeap());

        if(value1.getType().equals(new IntType())){
            IValue value2 = this.expression2.eval(state.getSymbolsTable(), state.getHeap());
            if(value2.getType().equals(new IntType())){
                if(state.getSymbolsTable().isDefined(this.variableName)){
                    IntValue intValue1 = (IntValue) value1;
                    IntValue intValue2 = (IntValue) value2;

                    state.getSymbolsTable().update(
                            this.variableName,
                            new IntValue(
                                    state.getSemaphoreTable().put(
                                            intValue1.getValue(),
                                            new LinkedList<>(),
                                            intValue2.getValue()
                                    )
                            )
                    );

                }else {
                    throw new VariableNotDefined(this.variableName);
                }
            }else{
                throw new MyException(this.expression2 + " must be an int");
            }
        }else{
            throw new MyException(this.expression1 + " must be an int");
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new CreateSemaphoreStatement(this.variableName, this.expression1.deepCopy(), this.expression2.deepCopy());
    }

    @Override
    public String toString() {
        return "newSemaphore(" + this.variableName + ", " + this.expression1 + ", " +this.expression2 + ");";
    }
}

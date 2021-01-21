package Model.Statements.SemaphoreStatements;

import Exceptions.MyException;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.IType;
import Model.Types.IntType;
import Model.Values.IValue;
import Model.Values.IntValue;

public class CreateSemaphoreStatement implements IStatement {
    String variableName;
    IExpression expression;

    public CreateSemaphoreStatement(String variableName, IExpression expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IValue value = this.expression.eval(state.getSymbolsTable(), state.getHeap());
        if(value.getType().equals(new IntType())){
            IntValue intValue = (IntValue) value;

            if(state.getSymbolsTable().isDefined(this.variableName)){
                value = state.getSymbolsTable().lookup(this.variableName);
                if(value.getType().equals(new IntType())){
                    state.getSymbolsTable().update(
                            this.variableName,
                            new IntValue(state.getSemaphoreTable().put(intValue.getValue())));
                }
                else{
                    throw new MyException("Variable must be an int");
                }
            }
            else{
                throw new MyException("Variable does not exist");
            }
        }
        else{
            throw new MyException("Expression must be an integer");
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        if(typeEnvironment.lookup(this.variableName).equals(new IntType())){
            IType expressionType = this.expression.typeCheck(typeEnvironment);
            if(!expressionType.equals(new IntType()))
                throw new MyException("Expression must be an int");
        }
        else{
            throw new MyException(this.variableName + " must be an int");
        }
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new CreateSemaphoreStatement(this.variableName, this.expression.deepCopy());
    }

    @Override
    public String toString() {
        return "createSemaphore(" + this.variableName + ", " + this.expression.toString() + ");";
    }
}

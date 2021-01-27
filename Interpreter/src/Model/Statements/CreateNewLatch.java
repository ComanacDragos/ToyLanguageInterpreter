package Model.Statements;

import Exceptions.MyException;
import Exceptions.VariableNotDefined;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.ProgramState;
import Model.Types.IType;
import Model.Types.IntType;
import Model.Values.IValue;
import Model.Values.IntValue;

public class CreateNewLatch implements IStatement{
    String variableName;
    IExpression expression;

    public CreateNewLatch(String variableName, IExpression expression) {
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
            IntValue intValue = (IntValue) value;

            if(state.getSymbolsTable().isDefined(this.variableName)){
                IValue varValue = state.getSymbolsTable().lookup(this.variableName);

                if(varValue.getType().equals(new IntType())){
                    state.getSymbolsTable().update(
                            this.variableName,
                            new IntValue(state.getLatchTable().put(intValue.getValue()))
                    );
                }else{
                    throw new MyException(this.variableName + " must be an int");
                }
            }else{
                throw new VariableNotDefined(this.variableName);
            }
        }else{
            throw new MyException(this.expression + " must be an int");
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        IType expressionType = this.expression.typeCheck(typeEnvironment);

        if(expressionType.equals(new IntType())){
            if(typeEnvironment.isDefined(this.variableName)){
                if(!typeEnvironment.lookup(this.variableName).equals(new IntType()))
                    throw new MyException(this.variableName + " must be an integer");
            }else{
                throw new VariableNotDefined(this.variableName);
            }
        }else{
            throw new MyException(this.expression + " must be an integer");
        }
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new CreateNewLatch(this.variableName, this.expression.deepCopy());
    }

    @Override
    public String toString() {
        return "newLatch(" + this.variableName + ", " + this.expression + ");";
    }
}

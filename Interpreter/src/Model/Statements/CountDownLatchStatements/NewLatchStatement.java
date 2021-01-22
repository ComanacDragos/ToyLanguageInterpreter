package Model.Statements.CountDownLatchStatements;

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

public class NewLatchStatement implements IStatement {
    String variableName;
    IExpression expression;

    public NewLatchStatement(String variableName, IExpression expression) {
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
        if(state.getSymbolsTable().isDefined(this.variableName)){
            if(state.getSymbolsTable().lookup(this.variableName).getType().equals(new IntType())) {
                IValue value = this.expression.eval(state.getSymbolsTable(), state.getHeap());
                if (value.getType().equals(new IntType())) {
                    IntValue intValue = (IntValue) value;
                    IntValue freeLocation = new IntValue(state.getCountDownLatchTable().put(intValue.getValue()));
                    state.getSymbolsTable().update(this.variableName, freeLocation);
                } else {
                    throw new MyException("Expression must be an int");
                }
            }
            else{
                throw new MyException(this.variableName + " must be an int");
            }
        }else{
            throw new VariableNotDefined(this.variableName);
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        if(typeEnvironment.isDefined(this.variableName)){
            if(typeEnvironment.lookup(this.variableName).equals(new IntType())) {
                IType expressionType = this.expression.typeCheck(typeEnvironment);
                if (!expressionType.equals(new IntType())){
                    throw new MyException("Expression must be an int");
                }
            }
            else{
                throw new MyException(this.variableName + " must be an int");
            }
        }else{
            throw new VariableNotDefined(this.variableName);
        }
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new NewLatchStatement(this.variableName, this.expression.deepCopy());
    }

    @Override
    public String toString() {
        return "newLatch(" + this.variableName + ", " + this.expression + ");";
    }
}

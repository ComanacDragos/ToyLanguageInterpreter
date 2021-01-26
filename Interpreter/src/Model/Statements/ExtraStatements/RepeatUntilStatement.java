package Model.Statements.ExtraStatements;

import Exceptions.MyException;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.Expressions.UnaryExpressions.NotExpression;
import Model.ProgramState;
import Model.Statements.CompoundStatement;
import Model.Statements.ControlFlowStatements.WhileStatement;
import Model.Statements.IStatement;
import Model.Types.BoolType;
import Model.Types.IType;

public class RepeatUntilStatement implements IStatement {
    IStatement statement;
    IExpression expression;

    public RepeatUntilStatement(IStatement statement, IExpression expression) {
        this.statement = statement;
        this.expression = expression;
    }

    public IStatement getStatement() {
        return statement;
    }

    public void setStatement(IStatement statement) {
        this.statement = statement;
    }

    public IExpression getExpression() {
        return expression;
    }

    public void setExpression(IExpression expression) {
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        state.getExecutionStack().push(
                new CompoundStatement(
                        this.statement,
                        new WhileStatement(
                                new NotExpression(this.expression),
                                this.statement
                        )
                )
        );
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        this.statement.typeCheck(typeEnvironment.shallowCopy());
        if(!this.expression.typeCheck(typeEnvironment).equals(new BoolType())){
            throw new MyException(this.expression + " must be a boolean");
        }
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new RepeatUntilStatement(this.statement.deepCopy(), this.expression.deepCopy());
    }

    @Override
    public String toString() {
        return "(repeat (" + this.statement + ") until " + this.expression + ");";
    }
}

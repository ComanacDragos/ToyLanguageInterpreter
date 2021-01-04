package Model.Statements.ExtraStatements;

import Exceptions.MyException;
import Exceptions.VariableNotDefined;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.ProgramState;
import Model.Statements.AssignStatement;
import Model.Statements.ControlFlowStatements.IfStatement;
import Model.Statements.IStatement;
import Model.Types.BoolType;
import Model.Types.IType;

public class ConditionalAssignmentStatement implements IStatement {
    String variableName;
    IExpression conditionalExpression, thenExpression, elseExpression;

    public ConditionalAssignmentStatement(String variableName, IExpression conditionalExpression, IExpression thenExpression, IExpression elseExpression) {
        this.variableName = variableName;
        this.conditionalExpression = conditionalExpression;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public IExpression getConditionalExpression() {
        return conditionalExpression;
    }

    public void setConditionalExpression(IExpression conditionalExpression) {
        this.conditionalExpression = conditionalExpression;
    }

    public IExpression getThenExpression() {
        return thenExpression;
    }

    public void setThenExpression(IExpression thenExpression) {
        this.thenExpression = thenExpression;
    }

    public IExpression getElseExpression() {
        return elseExpression;
    }

    public void setElseExpression(IExpression elseExpression) {
        this.elseExpression = elseExpression;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        state.getExecutionStack().push(
                new IfStatement(
                        this.conditionalExpression,
                        new AssignStatement(
                                this.variableName,
                                this.thenExpression
                        ),
                        new AssignStatement(
                                this.variableName,
                                this.elseExpression
                        )
                )
        );
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        IType conditionalExpressionType = this.conditionalExpression.typeCheck(typeEnvironment);
        if(conditionalExpressionType.equals(new BoolType())){
            IType variableType;
            try {
                variableType = typeEnvironment.lookup(this.variableName);
            }
            catch (MyException exc){
                throw new VariableNotDefined(this.variableName);
            }

            IType thenExpressionType = this.thenExpression.typeCheck(typeEnvironment);
            if(variableType.equals(thenExpressionType)){
                IType elseExpressionType = this.thenExpression.typeCheck(typeEnvironment);
                if(variableType.equals(elseExpressionType)){
                    return typeEnvironment;
                }
                else{
                    throw new MyException("Else expression must match " + this.variableName + " type");
                }
            }
            else{
                throw new MyException("Then expression must match " + this.variableName + " type");
            }
        }
        else{
            throw new MyException("Conditional expression must be a boolean");
        }
    }

    @Override
    public IStatement deepCopy() {
        return new ConditionalAssignmentStatement(this.variableName,
                this.conditionalExpression.deepCopy(),
                this.thenExpression.deepCopy(),
                this.elseExpression.deepCopy());
    }

    @Override
    public String toString() {
        return this.variableName + "=" + this.conditionalExpression + "?" + this.thenExpression + ":" + this.elseExpression + ";";
    }
}

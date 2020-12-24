package Model.Statements.ExtraStatements;

import Exceptions.MyException;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.IType;
import javafx.util.Pair;

import java.util.List;

public class SwitchStatement implements IStatement {
    IExpression conditionalExpression;
    List<Pair<IExpression, IStatement>> cases;

    public SwitchStatement(IExpression conditionalExpression, List<Pair<IExpression, IStatement>> cases){
        this.conditionalExpression = conditionalExpression;
        this.cases = cases;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        IType conditionalExpressionType = this.conditionalExpression.typeCheck(typeEnvironment);

        this.cases.forEach(
                c -> {
                    IType caseExpression = c.getKey().typeCheck(typeEnvironment);
                    /*if(!c.getKey()..equals(conditionalExpressionType)){

                    }*/
                }
        );
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new SwitchStatement(this.conditionalExpression, this.cases);
    }

    @Override
    public String toString() {
        StringBuilder stringSwitch = new StringBuilder();
        stringSwitch.append("switch(").append(this.conditionalExpression.toString()).append("){\n");
        this.cases.forEach(
                c -> stringSwitch
                .append("case ")
                .append(c.getKey().toString())
                .append(": \n{\n")
                .append(c.getValue().toString())
                .append("\n}\n")
        );

        return stringSwitch.toString();
    }
}

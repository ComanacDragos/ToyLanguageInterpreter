package Model.Statements.ExtraStatements;

import Exceptions.MyException;
import Model.ADTs.MyDictionary;
import Model.ADTs.MyIDictionary;
import Model.Expressions.IExpression;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.BoolType;
import Model.Types.IType;
import Model.Values.BoolValue;
import Model.Values.IValue;

import java.util.Map;
import java.util.stream.Collectors;

public class ForStatement implements IStatement {
    IStatement declarationStatement;
    IExpression conditionalExpression;
    IStatement incrementalStatement;
    IStatement bodyStatement;
    MyIDictionary<String, IValue> originalSymbolsTable;
    Boolean isDeclared;

    public ForStatement(IStatement declarationStatement, IExpression conditionalExpression, IStatement incrementalStatement, IStatement bodyStatement) {
        this.declarationStatement = declarationStatement;
        this.conditionalExpression = conditionalExpression;
        this.incrementalStatement = incrementalStatement;
        this.bodyStatement = bodyStatement;
        this.originalSymbolsTable = new MyDictionary<>();
        this.isDeclared = false;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        if(!this.isDeclared){
            state.getExecutionStack().push(this);
            state.getExecutionStack().push(this.declarationStatement);
            this.isDeclared = true;
            return null;
        }

        if(this.originalSymbolsTable.size() == 0){
            state.getSymbolsTable().stream().forEach(e -> this.originalSymbolsTable.put(e.getKey(), e.getValue()));
        }

        BoolValue boolValue = (BoolValue) this.conditionalExpression.eval(state.getSymbolsTable(), state.getHeap());

        if(boolValue.getValue()){
            state.getExecutionStack().push(this);
            state.getExecutionStack().push(this.incrementalStatement);
            state.getExecutionStack().push(this.bodyStatement);

            state.getSymbolsTable().setContent(
                    state.getSymbolsTable().stream()
                    .filter(e -> this.originalSymbolsTable.isDefined(e.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        this.declarationStatement.typeCheck(typeEnvironment);

        if(!this.conditionalExpression.typeCheck(typeEnvironment).equals(new BoolType())){
            throw new MyException("Conditional expression must be a boolean");
        }
        this.bodyStatement.typeCheck(typeEnvironment.shallowCopy());
        this.incrementalStatement.typeCheck(typeEnvironment);
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new ForStatement(
                this.declarationStatement.deepCopy(),
                this.conditionalExpression.deepCopy(),
                this.incrementalStatement.deepCopy(),
                this.bodyStatement.deepCopy()
        );
    }

    @Override
    public String toString() {
        return "for(" + this.declarationStatement.toString().replaceAll("\n", "") + " "
                + this.conditionalExpression.toString() + "; "
                + this.incrementalStatement.toString().replaceAll("[\n,;]", "") + "){\n"
                + this.bodyStatement
                + "\n}";
    }
}

package Model.Statements.LockStatements;

import Exceptions.MyException;
import Model.ADTs.MyIDictionary;
import Model.ADTs.MyLockTable;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.IType;

public class CreateLockStatement implements IStatement {
    String lockName;

    public CreateLockStatement(String lockName){
        this.lockName = lockName;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        MyLockTable lockTable = state.getLockTable();
        if(lockTable.isDefined(this.lockName))
            throw new MyException("Lock already defined");
        lockTable.put(this.lockName, Boolean.FALSE);

        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new CreateLockStatement(lockName);
    }

    @Override
    public String toString() {
        return "createLock(" + this.lockName + ");";
    }
}

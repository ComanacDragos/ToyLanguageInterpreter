package Model.Statements.LockStatements;

import Exceptions.MyException;
import Model.ADTs.MyIDictionary;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.IType;

public class ReleaseLockStatement implements IStatement {
    String lockName;

    public ReleaseLockStatement(String lockName) {
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
        MyIDictionary<String, Boolean> lockTable = state.getLockTable();

        if(lockTable.isDefined(this.lockName)){
            lockTable.put(this.lockName, Boolean.FALSE);
        }
        else{
            throw new MyException("Lock is not defined");
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new ReleaseLockStatement(this.lockName);
    }

    @Override
    public String toString() {
        return "releaseLock(" + this.lockName + ");";
    }
}

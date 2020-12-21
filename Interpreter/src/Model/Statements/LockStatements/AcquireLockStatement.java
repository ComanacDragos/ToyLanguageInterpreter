package Model.Statements.LockStatements;

import Exceptions.MyException;
import Model.ADTs.MyIDictionary;
import Model.ADTs.MyLockTable;
import Model.ProgramState;
import Model.Statements.IStatement;
import Model.Types.IType;

public class AcquireLockStatement implements IStatement {
    String lockName;

    public AcquireLockStatement(String lockName) {
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
        if(state.getLockTable().acquireLock(this.lockName)){
            state.getExecutionStack().push(this);
        }

        return null;
    }

    @Override
    public MyIDictionary<String, IType> typeCheck(MyIDictionary<String, IType> typeEnvironment) throws MyException {
        return typeEnvironment;
    }

    @Override
    public IStatement deepCopy() {
        return new AcquireLockStatement(this.lockName);
    }

    @Override
    public String toString() {
        return "acquireLock(" + this.lockName + ");";
    }
}

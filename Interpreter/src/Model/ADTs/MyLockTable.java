package Model.ADTs;

import Exceptions.MyException;

public class MyLockTable extends MyDictionary<String, Boolean>{
    //Returns true if the lock is acquired and false otherwise
    public Boolean acquireLock(String lockName){
        if(this.dictionary.containsKey(lockName)){
            synchronized (this.dictionary){
                if(this.dictionary.get(lockName)){
                    return true;
                }
                else{
                    this.dictionary.put(lockName, Boolean.TRUE);
                    return false;
                }
            }
        }
        else{
            throw new MyException("Lock is not defined");
        }
    }

    public void releaseLock(String lockName){
        if(this.dictionary.containsKey(lockName)){
            this.dictionary.put(lockName, Boolean.FALSE);
        }
        else{
            throw new MyException("Lock is not defined");
        }
    }
}

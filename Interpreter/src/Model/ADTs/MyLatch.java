package Model.ADTs;

import Exceptions.MyException;

import java.util.concurrent.atomic.AtomicInteger;

public class MyLatch extends MyDictionary<Integer, Integer>{
    AtomicInteger freeLocation = new AtomicInteger(0);

    @Override
    public synchronized Integer lookup(Integer integer) {
        return super.lookup(integer);
    }

    public synchronized Integer put(Integer integer) {
        super.put(freeLocation.incrementAndGet(), integer);
        return freeLocation.get();
    }

    public synchronized void countDown(Integer latch){
        if(this.isDefined(latch)){
            Integer value = this.lookup(latch);
            if(value>0)
                this.update(latch, value-1);
        }else{
            throw new MyException("Latch not defined");
        }
    }
}

package Model.ADTs;

import Exceptions.InexistentKey;

import java.util.concurrent.atomic.AtomicInteger;

public class MyCountDownLatchTable extends MyDictionary<Integer, Integer>{
    AtomicInteger freeLocation = new AtomicInteger(0);

    @Override
    public synchronized Integer lookup(Integer integer) {
        return super.lookup(integer);
    }

    @Override
    public synchronized void update(Integer integer, Integer integer2) throws InexistentKey {
        super.update(integer, integer2);
    }

    public synchronized Integer put(Integer integer) {
        int newAddress = this.freeLocation.incrementAndGet();
        super.put(newAddress, integer);
        return newAddress;
    }

    public synchronized void countDown(Integer latch){
        Integer latchValue = this.lookup(latch);
        if(latchValue > 0)
            this.update(latch, latchValue - 1);
    }
}

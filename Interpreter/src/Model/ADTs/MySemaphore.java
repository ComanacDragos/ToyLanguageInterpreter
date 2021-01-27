package Model.ADTs;

import Exceptions.MyException;
import Model.Values.BoolValue;
import javafx.util.Pair;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MySemaphore extends MyDictionary<Integer, Pair<Integer, Pair<List<Integer>, Integer>>>{
    AtomicInteger freeLocation = new AtomicInteger(0);

    @Override
    public synchronized Pair<Integer, Pair<List<Integer>, Integer>> lookup(Integer integer) {
        return super.lookup(integer);
    }

    public synchronized Integer put(Integer n1, List<Integer> ids, Integer n2) {
        Integer newFreeLocation = freeLocation.incrementAndGet();
        super.put(newFreeLocation, new Pair<>(n1, new Pair<>(ids, n2)));
        return newFreeLocation;
    }

    public synchronized Boolean acquire(Integer semaphoreId, Integer programId){
        if(this.isDefined(semaphoreId)){
            Pair<Integer, Pair<List<Integer>, Integer>> semaphore = this.lookup(semaphoreId);
            if(semaphore.getKey() - semaphore.getValue().getValue() > semaphore.getValue().getKey().size()){
                List<Integer> ids = semaphore.getValue().getKey();
                if(!ids.contains(programId)){
                    ids.add(programId);
                }
                return true;
            }
            return false;
        }else{
            throw new MyException("Semaphore not defined");
        }
    }

    public synchronized void release(Integer semaphoreId, Integer programId){
        if(this.isDefined(semaphoreId)){
            this.lookup(semaphoreId).getValue().getKey().remove(programId);
        }else{
            throw new MyException("Semaphore not defined");
        }
    }

    public static class SemaphoreEntry{
        Integer id;
        Integer n;
        String ids;

        public SemaphoreEntry(Integer id, Integer n1, Integer n2, List<Integer> ids) {
            this.id = id;
            this.n = n1-n2;
            this.ids = ids.stream().map(Objects::toString).collect(Collectors.joining(", "));
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getN() {
            return n;
        }

        public void setN(Integer n) {
            this.n = n;
        }

        public String getIds() {
            return ids;
        }

        public void setIds(String ids) {
            this.ids = ids;
        }

        @Override
        public String toString() {
            return this.id.toString() + ", " + this.ids + ", " + this.n;
        }
    }
}

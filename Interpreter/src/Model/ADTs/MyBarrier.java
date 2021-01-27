package Model.ADTs;

import Exceptions.MyException;
import Model.Values.BoolValue;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MyBarrier extends MyDictionary<Integer, Pair<Integer, List<Integer>>>{
    AtomicInteger freeLocation = new AtomicInteger(0);

    @Override
    public synchronized Pair<Integer, List<Integer>> lookup(Integer integer) {
        return super.lookup(integer);
    }

    public synchronized Integer put(Integer size) {
        Integer address = freeLocation.incrementAndGet();
        super.put(address, new Pair<>(size, new LinkedList<>()));
        return address;
    }

    public synchronized Boolean await(Integer barrier, Integer programId){
        if(this.isDefined(barrier)){
            Pair<Integer, List<Integer>> entry = this.lookup(barrier);

            if(entry.getKey() > entry.getValue().size()){
                if(!entry.getValue().contains(programId))
                    entry.getValue().add(programId);
                return true;
            }
            return false;
        }else{
            throw new MyException("Barrier not defined");
        }
    }

    public static class BarrierEntry{
        String ids;
        Integer id, size;

        public BarrierEntry(Integer id, Integer size, List<Integer> ids) {
            this.ids = ids.stream().map(Objects::toString).collect(Collectors.joining(", "));
            this.id = id;
            this.size = size;
        }

        public String getIds() {
            return ids;
        }

        public void setIds(String ids) {
            this.ids = ids;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }
    }
}

package Model.ADTs;

import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MySemaphore extends MyDictionary<Integer, Pair<Integer, List<Integer>>>{
    AtomicInteger freeLocation = new AtomicInteger(0);

    @Override
    public synchronized Pair<Integer, List<Integer>> lookup(Integer integer) {
        return super.lookup(integer);
    }

    public synchronized Integer put(Integer integer) {
        int newLocation = freeLocation.incrementAndGet();
        super.put(newLocation, new Pair<>(integer, new LinkedList<>()));
        return newLocation;
    }

    public synchronized Boolean acquire(Integer semaphore, Integer programId){
        Pair<Integer, List<Integer>> integerListPair = this.lookup(semaphore);
        if(integerListPair.getKey() > integerListPair.getValue().size()){
            if(!integerListPair.getValue().contains(programId)){
                integerListPair.getValue().add(programId);
            }
            return true;
        }else{
            return false;
        }
    }

    public synchronized void release(Integer semaphore, Integer programId){
        this.lookup(semaphore).getValue().remove(programId);
    }

    public static class SemaphoreEntry{
        Integer id, size;
        String programs;

        public SemaphoreEntry(Integer id, Integer size, List<Integer> programs) {
            this.id = id;
            this.size = size;
            this.programs = "";

            programs.stream()
                    .map(Objects::toString)
                    .reduce((a,b)-> a + ", " + b)
                    .ifPresent(s -> this.programs = s);
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

        public String getPrograms() {
            return programs;
        }

        public void setPrograms(String programs) {
            this.programs = programs;
        }
    }
}

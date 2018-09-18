package com.example.vangelis.connectingdevices.tasks;

import com.example.vangelis.connectingdevices.utilities.PrimeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class ConcurrentArraySumPrimes extends RecursiveTask<Long> {

    private static final int PARALLEL_CUTOFF = 1000;
    private int[] numbers;
    private int lowerIndex, upperIndex;

    public ConcurrentArraySumPrimes(int[] numbers, int lowerIndex, int upperIndex){
        this.numbers = numbers;
        this.lowerIndex = lowerIndex;
        this.upperIndex = upperIndex;
    }



    @Override
    protected Long compute() {
        int range = upperIndex - lowerIndex;
        if (range <= PARALLEL_CUTOFF) {
            return(PrimeUtils.countArrayPrimes(numbers, lowerIndex, upperIndex));
        } else {
            List<ConcurrentArraySumPrimes> subTasks = new ArrayList<>(createSubTasks());
            for(ConcurrentArraySumPrimes subTask : subTasks){
                subTask.fork();
            }
            long result = 0;
            for(ConcurrentArraySumPrimes subTask : subTasks){
                result += subTask.join();
            }

            return result;
        }
    }

    private List<ConcurrentArraySumPrimes> createSubTasks(){
        int range = upperIndex - lowerIndex;
        int middleIndex = lowerIndex + (range/2);
        List<ConcurrentArraySumPrimes> subTasks = new ArrayList<>();

        ConcurrentArraySumPrimes subTask1 = new ConcurrentArraySumPrimes(numbers, lowerIndex, middleIndex);
        ConcurrentArraySumPrimes subTask2 = new ConcurrentArraySumPrimes(numbers, middleIndex + 1, upperIndex);

        subTasks.add(subTask1);
        subTasks.add(subTask2);

        return subTasks;
    }

}

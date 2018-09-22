package com.example.vangelis.connectingdevices.tasks;

import com.example.vangelis.connectingdevices.utilities.PrimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class ConcurrentArrayCountPrimes extends RecursiveTask<Long> {

    private static final int PARALLEL_CUTOFF = 1000;
    private int[] numbers;
    private int lowerIndex, upperIndex;

    public ConcurrentArrayCountPrimes(int[] numbers, int lowerIndex, int upperIndex) {
        this.numbers = numbers;
        this.lowerIndex = lowerIndex;
        this.upperIndex = upperIndex;
    }


    @Override
    protected Long compute() {
        int range = upperIndex - lowerIndex;
        if (range <= PARALLEL_CUTOFF) {
            return (PrimeUtils.countArrayPrimes(numbers, lowerIndex, upperIndex));
        } else {
            List<ConcurrentArrayCountPrimes> subTasks = new ArrayList<>(createSubTasks());
            for (ConcurrentArrayCountPrimes subTask : subTasks) {
                subTask.fork();
            }
            long result = 0;
            for (ConcurrentArrayCountPrimes subTask : subTasks) {
                result += subTask.join();
            }

            return result;
        }
    }

    private List<ConcurrentArrayCountPrimes> createSubTasks() {
        int range = upperIndex - lowerIndex;
        int middleIndex = lowerIndex + (range / 2);
        List<ConcurrentArrayCountPrimes> subTasks = new ArrayList<>();

        ConcurrentArrayCountPrimes subTask1 = new ConcurrentArrayCountPrimes(numbers, lowerIndex, middleIndex);
        ConcurrentArrayCountPrimes subTask2 = new ConcurrentArrayCountPrimes(numbers, middleIndex + 1, upperIndex);

        subTasks.add(subTask1);
        subTasks.add(subTask2);

        return subTasks;
    }

}

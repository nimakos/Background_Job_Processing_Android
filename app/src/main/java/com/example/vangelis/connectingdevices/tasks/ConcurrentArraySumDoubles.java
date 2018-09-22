package com.example.vangelis.connectingdevices.tasks;

import com.example.vangelis.connectingdevices.utilities.SumUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class ConcurrentArraySumDoubles extends RecursiveTask<Double> {
    private static final int PARALLEL_CUTOFF = 1000;
    private double[] numbers;
    private int lowerIndex, upperIndex;

    public ConcurrentArraySumDoubles(double[] numbers, int lowerIndex, int upperIndex) {
        this.numbers = numbers;
        this.lowerIndex = lowerIndex;
        this.upperIndex = upperIndex;
    }


    @Override
    protected Double compute() {
        int range = upperIndex - lowerIndex;
        if (range <= PARALLEL_CUTOFF) {
            return (SumUtils.arraySum(numbers, lowerIndex, upperIndex));
        } else {
            List<ConcurrentArraySumDoubles> subTasks = new ArrayList<>(createSubTasks());
            for (ConcurrentArraySumDoubles subTask : subTasks) {
                subTask.fork();
            }
            double result = 0;
            for (ConcurrentArraySumDoubles subTask : subTasks) {
                result += subTask.join();
            }

            return result;
        }
    }

    private List<ConcurrentArraySumDoubles> createSubTasks() {
        int range = upperIndex - lowerIndex;
        int middleIndex = lowerIndex + (range / 2);
        List<ConcurrentArraySumDoubles> subTasks = new ArrayList<>();

        ConcurrentArraySumDoubles subTask1 = new ConcurrentArraySumDoubles(numbers, lowerIndex, middleIndex);
        ConcurrentArraySumDoubles subTask2 = new ConcurrentArraySumDoubles(numbers, middleIndex + 1, upperIndex);

        subTasks.add(subTask1);
        subTasks.add(subTask2);

        return subTasks;
    }
}

package com.example.vangelis.connectingdevices.tasks;

import com.example.vangelis.connectingdevices.calculations.SumUtils;
import java.util.concurrent.RecursiveTask;


public class ParallelArraySumDoubles extends RecursiveTask<Double> {

    private static final int PARALLEL_CUTOFF = 1000;
    private double[] numbers;
    private int lowerIndex, upperIndex;

    public ParallelArraySumDoubles(double[] numbers, int lowerIndex, int upperIndex)
    {
        this.numbers = numbers;
        this.lowerIndex = lowerIndex;
        this.upperIndex = upperIndex;
    }


    @Override
    protected Double compute() {
        int range = upperIndex - lowerIndex;
        if (range <= PARALLEL_CUTOFF) {
            return(SumUtils.arraySum(numbers, lowerIndex, upperIndex));
        } else {
            int middleIndex = lowerIndex + (range/2);
            ParallelArraySumDoubles leftSummer = new ParallelArraySumDoubles(numbers, lowerIndex, middleIndex);
            ParallelArraySumDoubles rightSummer = new ParallelArraySumDoubles(numbers, middleIndex+1, upperIndex);
            leftSummer.fork();
            Double rightSum = rightSummer.compute();
            Double leftSum = leftSummer.join();
            return(leftSum + rightSum);
        }
    }
}

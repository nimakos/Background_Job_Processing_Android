package com.example.vangelis.connectingdevices.tasks;

import com.example.vangelis.connectingdevices.utilities.PrimeUtils;
import java.util.concurrent.RecursiveTask;

public class ParallelArraySumPrimes extends RecursiveTask<Long> {

    private static final int PARALLEL_CUTOFF = 1000;
    private int[] numbers;
    private int lowerIndex, upperIndex;

    public ParallelArraySumPrimes(int[] numbers, int lowerIndex, int upperIndex){
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
            int middleIndex = lowerIndex + (range/2);
            ParallelArraySumPrimes leftSummer = new ParallelArraySumPrimes(numbers, lowerIndex, middleIndex);
            ParallelArraySumPrimes rightSummer = new ParallelArraySumPrimes(numbers, middleIndex+1, upperIndex);
            leftSummer.fork();
            Long rightSum = rightSummer.compute();
            Long leftSum = leftSummer.join();
            return(leftSum + rightSum);
        }
    }
}

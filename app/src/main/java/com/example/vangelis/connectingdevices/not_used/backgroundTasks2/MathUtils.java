package com.example.vangelis.connectingdevices.not_used.backgroundTasks2;

import java.util.concurrent.ForkJoinPool;

public class MathUtils {

    public static Double arraySumParallel(Double[] nums) {
        int parallelSizeCutoff = 1000;
        SequentialArrayProcessor<Double,Double> smallSizeProcessor = new ArrayAdder();
        Combiner<Double> valueCombiner = new Adder();
        ForkJoinPool pool = ParallelArrayProcessor.FORK_JOIN_POOL;
        ParallelArrayProcessor<Double,Double> processor = new ParallelArrayProcessor<>(nums, parallelSizeCutoff, smallSizeProcessor, valueCombiner,0, nums.length-1);
        return(pool.invoke(processor));
    }

    public static Double arraySum(Double[] nums, int lowerIndex, int upperIndex) {
        double sum = 0;
        for(int i = lowerIndex; i <= upperIndex; i++) {
            sum += nums[i];
        }
        return(sum);
    }
    public static Double arraySum(Double[] nums) {
        return(arraySum(nums, 0, nums.length-1));
    }

    public static Double[] randomNums2(int length) {
        Double[] nums = new Double[length];
        for(int i=0; i<length; i++) {
            nums[i] = Math.random();
        }
        return(nums);
    }
}


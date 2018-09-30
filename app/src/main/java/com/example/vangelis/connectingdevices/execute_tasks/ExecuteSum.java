package com.example.vangelis.connectingdevices.execute_tasks;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.vangelis.connectingdevices.tasks.ConcurrentArraySumDoubles;
import com.example.vangelis.connectingdevices.tasks.ParallelArraySumDoubles;
import com.example.vangelis.connectingdevices.calculations.SumUtils;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.DoubleStream;

public class ExecuteSum {

    private static ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    /**
     * Sequential sum without Streams
     *
     * @param numbers The array of doubles
     * @return The hole sum
     */
    public static double arraySum(double[] numbers) {
        return (SumUtils.arraySum(numbers, 0, numbers.length - 1));
    }

    /**
     * Sequential sum with streams
     *
     * @param numbers The array of doubles
     * @return The hole sum
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static double arraySumStream(double[] numbers) {
        return (DoubleStream.of(numbers).sum());
    }

    /**
     * Parallel sum with streams
     *
     * @param numbers The array of doubles
     * @return The hole sum
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static double arraySumParallelStream(double[] numbers) {
        return (DoubleStream.of(numbers).parallel().sum());
    }

    /**
     * Parallel sum without Streams
     *
     * @param numbers The array of doubles
     * @return The hole sum
     */
    public static double arraySumParallel(double[] numbers) {
        return (FORK_JOIN_POOL.invoke(new ParallelArraySumDoubles(numbers, 0, numbers.length - 1)));
    }

    public static double arraySumConcurrent(double[] numbers) {
        return (FORK_JOIN_POOL.invoke(new ConcurrentArraySumDoubles(numbers, 0, numbers.length - 1)));
    }
}

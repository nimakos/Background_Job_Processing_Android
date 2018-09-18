package com.example.vangelis.connectingdevices.execute_tasks;

import com.example.vangelis.connectingdevices.tasks.ConcurrentArraySumPrimes;
import com.example.vangelis.connectingdevices.tasks.ParallelArraySumPrimes;
import com.example.vangelis.connectingdevices.utilities.PrimeUtils;

import java.util.concurrent.ForkJoinPool;

public class ExecutePrimes {

    private static ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    /**
     * Sequential sum of Primes without Streams
     * @param numbers The array of doubles
     * @return The hole sum of primes
     */
    public static long sumArrayPrimes(int[] numbers){
        return PrimeUtils.countArrayPrimes(numbers, 0, numbers.length - 1);
    }

    /**
     * Parallel sum of Primes without Streams
     * @param numbers The array of doubles
     * @return The hole sum of primes
     */
    public static Long arrayOfPrimesSumParallel(int[] numbers) {
        return (FORK_JOIN_POOL.invoke(new ParallelArraySumPrimes(numbers,0,numbers.length-1)));
    }

    /**
     * Concurrent sum of Primes without Streams
     * @param numbers The array of doubles
     * @return The hole sum of primes
     */
    public static Long arrayOfPrimesSumConcurrent(int[] numbers) {

        return (FORK_JOIN_POOL.invoke(new ConcurrentArraySumPrimes(numbers,0,numbers.length-1)));
    }


}

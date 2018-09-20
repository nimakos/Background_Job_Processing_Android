package com.example.vangelis.connectingdevices.execute_tasks;

import com.example.vangelis.connectingdevices.tasks.ConcurrentArrayCountPrimes;
import com.example.vangelis.connectingdevices.tasks.ParallelArrayCountPrimes;
import com.example.vangelis.connectingdevices.utilities.PrimeUtils;

import java.util.concurrent.ForkJoinPool;

public class ExecutePrimes {

    private static ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    /**
     * Sequential sum of Primes without Streams
     * @param numbers The array of doubles
     * @return The hole sum of primes
     */
    public static long arrayOfPrimesSumSerial(int[] numbers){
        return PrimeUtils.countArrayPrimes(numbers, 0, numbers.length - 1);
    }

    /**
     * Parallel sum of Primes without Streams
     * @param numbers The array of doubles
     * @return The hole sum of primes
     */
    public static long arrayOfPrimesSumParallel(int[] numbers) {
        return (FORK_JOIN_POOL.invoke(new ParallelArrayCountPrimes(numbers,0,numbers.length-1)));
    }

    /**
     * Concurrent sum of Primes without Streams
     * @param numbers The array of doubles
     * @return The hole sum of primes
     */
    public static long arrayOfPrimesSumConcurrent(int[] numbers) {
        return (FORK_JOIN_POOL.invoke(new ConcurrentArrayCountPrimes(numbers,0,numbers.length-1)));
    }
}

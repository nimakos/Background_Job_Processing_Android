package com.example.vangelis.connectingdevices.calculations;

public class PrimeUtils {

    /**
     * Counts all the prime numbers from a given list and count the primes only
     *
     * @param numbers    The list of all the numbers
     * @param lowerIndex The lower index of the list
     * @param upperIndex The highest index of the list
     * @return The counted primes
     */
    public static long countArrayPrimes(int[] numbers, int lowerIndex, int upperIndex) {
        long sum = 0;
        for (int i = lowerIndex; i <= upperIndex; i++) {
            sum += (isPrime(numbers[i])) ? 1 : 0;
        }
        return (sum);
    }

    /**
     * Find if a given number is Prime
     *
     * @param n The given number
     * @return The result if is prime or not
     */
    private static boolean isPrime(long n) {
        if (n <= 1) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    /**
     * Put random integer numbers into the array
     *
     * @param length The length of the array
     * @return An array full of random integers
     */
    public static int[] randomIntegers(int length) {
        int Min = 0;
        int Max = 1000;
        int[] numbers = new int[length];
        for (int i = 0; i < length; i++) {
            numbers[i] = Min + (int) (Math.random() * ((Max - Min) + 1));
        }
        return (numbers);
    }
}

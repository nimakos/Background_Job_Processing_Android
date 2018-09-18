package com.example.vangelis.connectingdevices.utilities;

public class SumUtils {

    /**
     * Sum the index of an array full of doubles sequentially
     * @param numbers The array
     * @param lowerIndex Where to start the sum
     * @param upperIndex Where to stop the sum
     * @return The final result of summing
     */
    public static double arraySum(double[] numbers, int lowerIndex, int upperIndex) {
        double sum = 0;
        for(int i=lowerIndex; i<=upperIndex; i++) {
            sum += numbers[i];
        }
        return(sum);
    }

    /**
     * Put random double numbers into the array
     * @param length The length of the array
     * @return An array full of random doubles
     */
    public static double[] randomDoubles(int length) {
        double[] numbers = new double[length];
        for(int i=0; i < length; i++) {
            numbers[i] = Math.random();
        }
        return(numbers);
    }
}

package com.example.vangelis.connectingdevices.chunck;

public class ChunkPrimes {

    /**
     * @param array Takes the initial 1D array we want to cut in pieces based in the number of clients and create a 2D array the number of rows are the clients
     * @param chunkSize The number of array elements in each client
     * @return A 2D array representing in each row the array corresponding for each client and each column the number of elements
     */
    private static int[][] chunkPrimeArray(int[] array, int chunkSize) {
        int numOfChunks = (int)Math.ceil((double)array.length / chunkSize);
        int[][] output = new int[numOfChunks][];

        for(int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);

            int[] temp = new int[length];
            System.arraycopy(array, start, temp, 0, length);
            output[i] = temp;
        }
        return output;
    }

    /**
     * Converts the initial 1D array to 1D array for each client
     * @param array The initial array
     * @param chunkSize The number of array elements in each client
     * @param clientNumber The number of clients we have
     * @return 1D array for each client
     */
    public static int [] onePrimeArray(int[] array, int chunkSize, int clientNumber){
        int [][] output = chunkPrimeArray(array,chunkSize);
        int [] finalArray = new int[0];

        for(int i = clientNumber; i <= clientNumber; i++){
            finalArray = new int[output[i].length];
            System.arraycopy(output[i], 0, finalArray, 0, output[i].length);
        }
        return finalArray;
    }
}

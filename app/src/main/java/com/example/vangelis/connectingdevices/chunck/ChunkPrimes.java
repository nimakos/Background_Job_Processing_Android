package com.example.vangelis.connectingdevices.chunck;

public class ChunkPrimes {

    /**
     * @param initialArray Takes the initial 1D array we want to cut in pieces based in the number of clients and create a 2D array the number of rows are the clients
     * @param chunkSize The number of array elements in each client
     * @return A 2D array representing in each row the array corresponding for each client and each column the number of elements
     */
    private static int[][] chunkPrimeArray(int[] initialArray, int chunkSize) {
        int numOfChunks = (int)Math.ceil((double)initialArray.length / chunkSize);
        int[][] finalArray = new int[numOfChunks][];
        try {
            for (int i = 0; i < numOfChunks; ++i) {
                int start = i * chunkSize;
                int length = Math.min(initialArray.length - start, chunkSize);

                int[] tempArray = new int[length];
                System.arraycopy(initialArray, start, tempArray, 0, length);
                finalArray[i] = tempArray;
            }
        }catch (OutOfMemoryError om){
            om.printStackTrace();
        }
        return finalArray;
    }

    /**
     * Converts the initial 1D array to 1D array for each client
     * @param initialArray The initial array
     * @param chunkSize The number of array elements in each client
     * @param clientNumber The number of clients we have
     * @return 1D array for each client
     */
    public static int [] onePrimeArray(int[] initialArray, int chunkSize, int clientNumber){
        int [][] output = chunkPrimeArray(initialArray,chunkSize);
        int [] finalArray = new int[0];
        try {
            for (int i = clientNumber; i <= clientNumber; i++) {
                finalArray = new int[output[i].length];
                System.arraycopy(output[i], 0, finalArray, 0, output[i].length);
            }
        }catch (NullPointerException | OutOfMemoryError no){
            no.printStackTrace();
        }
        return finalArray;
    }
}

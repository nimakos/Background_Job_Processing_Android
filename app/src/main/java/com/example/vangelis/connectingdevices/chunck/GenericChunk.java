package com.example.vangelis.connectingdevices.chunck;

public class GenericChunk {

    private static <T> T[][] chunkGenericArray(T[] array, int chunkSize) {
        int numOfChunks = (int) Math.ceil((double) array.length / chunkSize);
        Object[][] output = new Object[numOfChunks][];

        for (int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);

            Object[] temp = new Object[length];
            System.arraycopy(array, start, temp, 0, length);
            output[i] = temp;
        }
        return (T[][]) output;
    }

    public static <T> Object[] oneGenericArray(T[] array, int chunkSize, int clientNumber) {
        T[][] output = chunkGenericArray(array, chunkSize);
        Object[] finalArray = new Object[0];
        try {
            for (int i = clientNumber; i <= clientNumber; i++) {
                finalArray = new Object[output[i].length];
                System.arraycopy(output[i], 0, finalArray, 0, output[i].length);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return finalArray;
    }
}

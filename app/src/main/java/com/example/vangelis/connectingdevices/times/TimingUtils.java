package com.example.vangelis.connectingdevices.times;

import android.util.Log;

import java.util.Locale;

public class TimingUtils {

    private static final double ONE_BILLION = 1_000_000_000;

    public static String timeOp(Op operation) {
        long startTime = System.nanoTime();
        String resultMessage = operation.runOp();
        long endTime = System.nanoTime();
        //System.out.println(resultMessage);
        Log.e("FORK_JOIN RESULT : ", resultMessage);
        double elapsedSeconds = (endTime - startTime) / ONE_BILLION;
        //System.out.printf("Elapsed time: %.3f seconds.%n", elapsedSeconds);
        String resultMessage2 = String.format(Locale.ENGLISH, "elapsed time is %.3f seconds.%n", elapsedSeconds);
        Log.e("FORK_JOIN RESULT : ", resultMessage2);

        return resultMessage2;
    }
}

package com.example.vangelis.connectingdevices.mapping_services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.example.vangelis.connectingdevices.chunck.ChunkSum;
import com.example.vangelis.connectingdevices.execute_tasks.ExecuteSum;
import com.example.vangelis.connectingdevices.model.ClientModel;
import com.example.vangelis.connectingdevices.times.TimingUtils;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static com.example.vangelis.connectingdevices.constans.Constants.SUM;

public class MappingSum extends Service {

    public static double[] arrayToCalculate;
    public static String wholeMessage;
    public static String time;

    /**
     * Create the array for processing and putting it into the hasMap for each client of hashMap
     *
     * @param mp          The hashMap
     * @param createArray The array for process
     * @return The final hashMap
     */
    public static Map<String, ClientModel> putArray(Map<String, ClientModel> mp, double[] createArray) {
        Iterator<Map.Entry<String, ClientModel>> it = mp.entrySet().iterator();
        int j = 0;
        while (it.hasNext()) {
            Map.Entry<String, ClientModel> pair = it.next();
            //Στρογγυλοποίηση προς τα πάνω (το μέγεθος του πίνακα που έχουμε προς τον αριθμό των clients) μας δίνει τον αριθμό των στοιχείων που θα έχει ο κάθε πίνακας
            int chunk = (int) Math.ceil((double) createArray.length / (mp.size()));
            double[] one = ChunkSum.oneArray(createArray, chunk, j);
            j++;
            pair.getValue().setChunkedArray(one);
        }
        return mp;
    }

    /**
     * Put the time in hashMap for each client to know when the background job has been started
     *
     * @param mp   The hashMap
     * @param time The startTime
     * @return The final hashMap with the started time
     */
    public static Map<String, ClientModel> putTime(Map<String, ClientModel> mp, long time) {
        for (Map.Entry<String, ClientModel> pair : mp.entrySet()) {
            pair.getValue().setStartTime(time);
        }
        return mp;
    }


    /**
     * Calculate and measure the time
     *
     * @param arr The array to be calculated
     * @return The double result from the calculation
     */
    public static double efficientCalculation(double[] arr, String kindOfCalculation) {
        final double[] clientResult = new double[1];
        switch (kindOfCalculation) {
            case "Serial": {
                String messageForSum = "Serial sum of %,d numbers is %,.4f.";
                time = TimingUtils.timeOp(() -> {
                    clientResult[0] = ExecuteSum.arraySum(arr);
                    wholeMessage = String.format(Locale.ENGLISH, messageForSum, arr.length, clientResult[0]);
                    return (wholeMessage);
                });
                wholeMessage += "\n" + time;
                break;
            }
            case "Concurrent": {
                String messageForSum = "Concurrent sum of %,d numbers is %,.4f.";
                time = TimingUtils.timeOp(() -> {
                    clientResult[0] = ExecuteSum.arraySumConcurrent(arr);
                    wholeMessage = String.format(Locale.ENGLISH, messageForSum, arr.length, clientResult[0]);
                    return (wholeMessage);
                });
                wholeMessage += "\n" + time;
                break;
            }
            case "Parallel": {
                String messageForSum = "Parallel sum of %,d numbers is %,.4f.";
                time = TimingUtils.timeOp(() -> {
                    clientResult[0] = ExecuteSum.arraySumParallel(arr);
                    wholeMessage = String.format(Locale.ENGLISH, messageForSum, arr.length, clientResult[0]);
                    return (wholeMessage);
                });
                wholeMessage += "\n" + time;
                break;
            }
        }
        return clientResult[0];
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String kindOfCalculation = intent.getStringExtra("kindOfCalculation");
            double result = efficientCalculation(arrayToCalculate, kindOfCalculation);

            //pass the result back
            ResultReceiver receiver = intent.getParcelableExtra("receiver");
            Bundle bundle = new Bundle();
            bundle.putDouble("message", result);
            bundle.putString("holeMessage", wholeMessage);
            receiver.send(SUM, bundle);
        } catch (NullPointerException np) {
            np.printStackTrace();
        }
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

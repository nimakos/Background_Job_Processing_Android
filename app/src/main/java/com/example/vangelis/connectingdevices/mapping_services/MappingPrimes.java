package com.example.vangelis.connectingdevices.mapping_services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.example.vangelis.connectingdevices.chunck.ChunkPrimes;
import com.example.vangelis.connectingdevices.execute_tasks.ExecutePrimes;
import com.example.vangelis.connectingdevices.model.ClientModel;
import com.example.vangelis.connectingdevices.times.Op;
import com.example.vangelis.connectingdevices.times.TimingUtils;

import java.util.Iterator;
import java.util.Map;

import static com.example.vangelis.connectingdevices.utilities.Constants.PRIME;


public class MappingPrimes extends Service {

    public static int [] arrayToCalculate;
    public static String holleMessage;
    public static String time;
    /**
     * Create the array for processing and putting it into the hasMap for each client of hashMap
     * @param mp The hashMap
     * @param createArray The array for process
     * @return The final hashMap
     */
    public static Map<String, ClientModel> putPrimeArray(Map<String, ClientModel> mp, int [] createArray){
        Iterator<Map.Entry<String, ClientModel>> it = mp.entrySet().iterator();
        int j = 0;
        while (it.hasNext()) {
            Map.Entry<String, ClientModel> pair = it.next();
            //Στρογγυλοποίηση προς τα πάνω (το μέγεθος του πίνακα που έχουμε προς τον αριθμό των clients) μας δίνει τον αριθμό των στοιχείων που θα έχει ο κάθε πίνακας
            int chunk = (int)Math.ceil((double)createArray.length / (mp.size()));
            int[] one = ChunkPrimes.onePrimeArray(createArray, chunk, j);
            j++;
            pair.getValue().setPrimeChunkedArray(one);
        }
        return mp;
    }

    /**
     * Put the time in hashMap for each client to know when the background job has been started
     * @param mp The hashMap
     * @param time The startTime
     * @return The final hashMap with the started time
     */
    public static Map<String, ClientModel> putTime(Map<String, ClientModel> mp, long time){
        for (Map.Entry<String, ClientModel> pair : mp.entrySet()) {
            pair.getValue().setStartTime(time);
        }
        return mp;
    }

    /**
     * Calculate and measure the time
     * @param arr The array to be calculated
     * @return The long result from the calculation
     */
    public static long efficientCalculation(int [] arr, String kindOfCalculation){
        final long[] clientResult = new long[1];
        switch (kindOfCalculation){
            case "Serial": {
                String messageForLong = "Serial Count of %,d prime numbers is %d and ";
                time =  TimingUtils.timeOp(new Op() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public String runOp() {
                        clientResult[0] = ExecutePrimes.arrayOfPrimesSumSerial(arr);
                        holleMessage = String.format(messageForLong, arr.length, clientResult[0]);
                        return (holleMessage);
                    }
                });
                holleMessage += "\n" + time;
                break;
            }
            case "Concurrent": {
                String messageForLong = "Concurrent sum of %,d prime numbers is %d and";
                time = TimingUtils.timeOp(new Op() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public String runOp() {
                        clientResult[0] = ExecutePrimes.arrayOfPrimesSumConcurrent(arr);
                        holleMessage = String.format(messageForLong, arr.length, clientResult[0]);
                        return (holleMessage);
                    }
                });
                holleMessage += "\n" + time;
                break;
            }
            case "Parallel": {
                String messageForLong = "Parallel sum of %,d prime numbers is %d and";
                time = TimingUtils.timeOp(new Op() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public String runOp() {
                        clientResult[0] = ExecutePrimes.arrayOfPrimesSumParallel(arr);
                        holleMessage = String.format(messageForLong, arr.length, clientResult[0]);
                        return (holleMessage);
                    }
                });
                holleMessage += "\n" + time;
                break;
            }
        }
        return clientResult[0];
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
             String kindOfCalculation = intent.getStringExtra("kindOfCalculation");
             long result = efficientCalculation(arrayToCalculate, kindOfCalculation);

             //pass the result back
             ResultReceiver receiver = intent.getParcelableExtra("receiver");
             Bundle bundle = new Bundle();
             bundle.putLong("message", result);
             bundle.putString("holeMessage", holleMessage);
             receiver.send(PRIME, bundle);
        }catch (NullPointerException np){
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

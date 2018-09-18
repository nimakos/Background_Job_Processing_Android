package com.example.vangelis.connectingdevices.mapping_services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.example.vangelis.connectingdevices.chunck.ChunkSum;
import com.example.vangelis.connectingdevices.execute_tasks.ExecuteSum;
import com.example.vangelis.connectingdevices.model.ClientModel;
import com.example.vangelis.connectingdevices.times.Op;
import com.example.vangelis.connectingdevices.times.TimingUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public class MappingSum extends Service {

    public static Map<String, ClientModel> map;

    /**
     * Create the array for processing and putting it into the hasMap for each client of hashMap
     * @param mp The hashMap
     * @param createArray The array for process
     * @return The final hashMap
     */
    public static Map<String, ClientModel> putArray(Map<String, ClientModel> mp, double [] createArray){
        Iterator<Map.Entry<String, ClientModel>> it = mp.entrySet().iterator();
        int j = 0;
        while (it.hasNext()) {
            Map.Entry<String, ClientModel> pair = it.next();
            //Στρογγυλοποίηση προς τα πάνω (το μέγεθος του πίνακα που έχουμε προς τον αριθμό των clients) μας δίνει τον αριθμό των στοιχείων που θα έχει ο κάθε πίνακας
            int chunk = (int)Math.ceil((double)createArray.length / (mp.size()));
            double[] one = ChunkSum.oneArray(createArray, chunk, j);
            j++;
            pair.getValue().setChunkedArray(one);
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
     * This is where all the calculations are been done for each client and measuring the time
     * @param mp The initial hashMap
     * @param ip The ip for each client in order to compare
     * @return The final hashMap with the time taken to do the job ONLY, and the result for each client
     */
    public static Map<String, ClientModel> configResult(Map<String, ClientModel> mp, String ip){
        Iterator<Map.Entry<String, ClientModel>> it = mp.entrySet().iterator();
        String message = "Parallel sum of %,d numbers is %,.4f.";
        while (it.hasNext()) {
            Map.Entry<String, ClientModel> pair = it.next();
            if(pair.getKey().equals(ip)){
                final double[] clientResult = new double[1];
                //compare times
                TimingUtils.timeOp(new Op() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public String runOp() {
                        double [] array = pair.getValue().getChunkedArray();
                        clientResult[0] = ExecuteSum.arraySumParallel(array); //doing the forkJoin
                        return (String.format(message, array.length, clientResult[0]));
                    }
                });
                pair.getValue().setResultFromSumArray(clientResult[0]);
                pair.getValue().setHasCompleteTheJob(true);
            }
        }
        return mp;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String ip = intent.getStringExtra("Ip");
            Map<String, ClientModel> mapping =  configResult(map, ip); //do the job

            //pass the result back
            ResultReceiver receiver = intent.getParcelableExtra("receiver");
            Bundle bundle = new Bundle();
            bundle.putSerializable("message", (Serializable) mapping);
            receiver.send(1234, bundle);

        }catch (NullPointerException np){
            np.printStackTrace();
        }
        return Service.START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

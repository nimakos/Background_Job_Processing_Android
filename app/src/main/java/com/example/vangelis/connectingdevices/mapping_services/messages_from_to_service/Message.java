package com.example.vangelis.connectingdevices.mapping_services.messages_from_to_service;

import android.os.Bundle;

import com.example.vangelis.connectingdevices.io.ReadWrite;

import java.io.IOException;
import java.util.List;

import static com.example.vangelis.connectingdevices.utilities.Constants.PRIME;
import static com.example.vangelis.connectingdevices.utilities.Constants.SEND_RESULT_BACK_TO_SERVER;
import static com.example.vangelis.connectingdevices.utilities.Constants.SUM;

public class Message {
    private String ipFromClient;
    private List<ReadWrite> readWrites;

    public Message(String ipFromClient, List<ReadWrite> readWrites){
        this.readWrites = readWrites;
        this.ipFromClient = ipFromClient;
    }
    @SuppressWarnings("unchecked")
    public void displayMessage(int resultCode, Bundle resultData){
        if(resultCode == PRIME) {
            long result = resultData.getLong("message");
            new Thread(() -> {
                for (ReadWrite i : readWrites) {
                    try {
                        i.writeStringPrimes(SEND_RESULT_BACK_TO_SERVER, ipFromClient, result, PRIME); //write the values back to server
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else if(resultCode == SUM){
            double result = resultData.getDouble("message");
            new Thread(() -> {
                for (ReadWrite i : readWrites) {
                    try {
                        i.writeStringDoubles(SEND_RESULT_BACK_TO_SERVER, ipFromClient, result, SUM); //write the values back to server
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}

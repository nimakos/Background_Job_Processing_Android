package com.example.vangelis.connectingdevices.mapping_services.messages_from_to_service;

import android.os.Bundle;

import com.example.vangelis.connectingdevices.io.ReadWrite;

import java.io.IOException;
import java.util.List;

import static com.example.vangelis.connectingdevices.utilities.Constants.SEND_ARRAY_TO_SERVER_EFFICIENTLY;

public class Message {
    private String ipFromClient;
    private List<ReadWrite> readWrites;

    public Message(String ipFromClient, List<ReadWrite> readWrites){
        this.readWrites = readWrites;
        this.ipFromClient = ipFromClient;
    }
    @SuppressWarnings("unchecked")
    public void displayMessage(int resultCode, Bundle resultData){
        long result = resultData.getLong("message");

        new Thread(() -> {
            for(ReadWrite i : readWrites){
                try {
                    i.write(SEND_ARRAY_TO_SERVER_EFFICIENTLY, ipFromClient, result); //write the values back to server
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

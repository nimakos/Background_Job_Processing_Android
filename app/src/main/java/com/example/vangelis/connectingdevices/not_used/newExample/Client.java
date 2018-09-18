package com.example.vangelis.connectingdevices.not_used.newExample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client extends Thread {
    protected Socket client;
    protected BufferedReader in;
    private String hostName;
    private int port;

    public Client(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
        this.start();
    }

    public void run(){
        try {
            this.client = new Socket(this.hostName, this.port);
            this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            String buffer;
            while ((buffer = in.readLine()) != null) {
                System.out.println(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

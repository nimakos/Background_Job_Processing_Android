package com.example.vangelis.connectingdevices.not_used.newExample;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler {
    protected Socket client;
    protected PrintWriter out;

    ClientHandler(Socket client) {
        this.client = client;
        try {
            this.out = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

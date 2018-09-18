package com.example.vangelis.connectingdevices.not_used.newExample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class SendMessage extends Thread {
    private List<ClientHandler> clients;
    private String userInput;
    private BufferedReader console;

    SendMessage(List<ClientHandler> clients) {
        this.clients = clients;
        this.userInput = "HELLO";
        this.start();
    }

    public void run() {
        System.out.println("New Communication Thread Started");
        if (clients.size() == 1) {
            System.out.println("Enter message:");
        }
        try {
            if (clients.size() > 0) {
                this.console = new BufferedReader(new InputStreamReader(System.in));
                while ((this.userInput = console.readLine()) != null) {
                    if (userInput.length() > 0) {
                        for (ClientHandler client : clients) {
                            client.out.println(userInput);
                            client.out.flush();
                            Thread.currentThread();
                            Thread.sleep(1000);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.example.vangelis.connectingdevices.not_used.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {

    private ConnectionToServer server;
    private LinkedBlockingQueue<Object> messages;
    private Socket socket;
    private String IPAddress;

    public Client(InetAddress hostAddress, int port) throws IOException {
        IPAddress = hostAddress.getHostAddress();
        socket = new Socket(IPAddress, port);
        messages = new LinkedBlockingQueue<>();
        server = new ConnectionToServer(socket);

        Thread messageHandling = new Thread() {
            public void run(){
                while(true){
                    try{
                        Object message = messages.take();
                        System.out.println("Message Received: " + message);
                    }
                    catch(InterruptedException e){e.printStackTrace(); }
                }
            }
        };

        messageHandling.setDaemon(true);
        messageHandling.start();
    }

    private class ConnectionToServer {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket;

        ConnectionToServer(Socket socket) throws IOException {
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            Thread read = new Thread(){
                public void run(){
                    while(true){
                        try{
                            Object obj = in.readObject();
                            messages.put(obj);
                        }
                        catch(IOException | InterruptedException | ClassNotFoundException e){ e.printStackTrace(); }
                    }
                }
            };

            read.setDaemon(true);
            read.start();
        }

        private void write(Object obj) {
            try{
                out.writeObject(obj);
            }
            catch(IOException e){ e.printStackTrace(); }
        }
    }

    public void send(Object obj) {
        server.write(obj);
    }
}

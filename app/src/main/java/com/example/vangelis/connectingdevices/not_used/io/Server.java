package com.example.vangelis.connectingdevices.not_used.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Server extends Thread {
    private ArrayList<ConnectionToClient> clientList;
    private LinkedBlockingQueue<Object> messages;
    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        clientList = new ArrayList<>();
        messages = new LinkedBlockingQueue<>();
        serverSocket = new ServerSocket(port);

        Thread accept = new Thread() {
            public void run(){
                while(true){
                    try{
                        Socket s = serverSocket.accept();
                        clientList.add(new ConnectionToClient(s));
                        System.out.print(clientList);
                    }
                    catch(IOException e){ e.printStackTrace(); }
                }
            }
        };
        accept.setDaemon(true);
        accept.start();

        Thread messageHandling = new Thread() {
            public void run(){
                while(true){
                    try{
                        Object message = messages.take();
                        // Do some handling here...
                        System.out.println("Message Received: " + message);
                    }
                    catch(InterruptedException e){e.printStackTrace(); }
                }
            }
        };

        messageHandling.setDaemon(true);
        messageHandling.start();
    }

    public void run(){
        sendToAll("HALLO");
    }

    private class ConnectionToClient extends Thread {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket;

        ConnectionToClient(Socket socket) throws IOException {
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
                        catch(IOException | InterruptedException | ClassNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            };

            read.setDaemon(true); // terminate when main ends
            read.start();
        }

        private void write(Object obj) {
            try{
                out.writeObject(obj);
            }
            catch(IOException e){ e.printStackTrace(); }
        }
    }

    public void sendToOne(int index, Object message) throws IndexOutOfBoundsException {
        clientList.get(index).write(message);
    }

    public void sendToAll(Object message){
        for(ConnectionToClient client : clientList)
            client.write(message);
    }

}

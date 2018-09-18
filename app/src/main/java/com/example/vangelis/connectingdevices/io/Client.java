package com.example.vangelis.connectingdevices.io;

import com.example.vangelis.connectingdevices.MainActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import static com.example.vangelis.connectingdevices.MainActivity.deviceAddress;
import static com.example.vangelis.connectingdevices.MainActivity.readWrites;

/**
 * Accepting the request from the Server, the client
 * creates a socket for communication with the Server
 * Client knowing both the address of the server and the port number
 * to which it wants to have access, sends a login request
 */
public class Client implements Runnable {
    private Socket clientSocket;
    private String hostAdd;
    private int serverPort;
    private MainActivity context;
    private static final int SEND_DEVICE_MAC_TO_SERVER = 4;

    public Client(InetAddress hostAddress, int serverPort, MainActivity context){
        this.context = context;
        hostAdd = hostAddress.getHostAddress();
        clientSocket = new Socket();
        try {
            clientSocket.setTcpNoDelay(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try {//connection with the server
            clientSocket.connect(new InetSocketAddress(hostAdd, serverPort), 500);

            ReadWrite readWrite = new ReadWrite(clientSocket, context);
            new Thread(readWrite).start();
            readWrites.add(readWrite); //holding the list of server device to send data

            //sends the device names back to the server after connection established
            for (ReadWrite j : readWrites) {
                j.writeObject(SEND_DEVICE_MAC_TO_SERVER, deviceAddress);
            }
        }catch (IOException e) {
            if(clientSocket != null){
                try{
                    clientSocket.close();
                }catch (IOException io){
                    io.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }
}


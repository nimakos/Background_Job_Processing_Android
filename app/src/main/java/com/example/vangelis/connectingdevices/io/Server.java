package com.example.vangelis.connectingdevices.io;

import android.view.View;
import android.widget.TextView;

import com.example.vangelis.connectingdevices.MainActivity;
import com.example.vangelis.connectingdevices.R;
import com.example.vangelis.connectingdevices.model.ClientModel;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static com.example.vangelis.connectingdevices.utilities.Constants.clientCounter;
import static com.example.vangelis.connectingdevices.utilities.Constants.deviceMacAddresses;
import static com.example.vangelis.connectingdevices.utilities.Constants.deviceArray;
import static com.example.vangelis.connectingdevices.utilities.Constants.hasSendMacAddress;
import static com.example.vangelis.connectingdevices.utilities.Constants.information;
import static com.example.vangelis.connectingdevices.utilities.Constants.mapClients;
import static com.example.vangelis.connectingdevices.utilities.Constants.readWrites;

/**
 * Server creates a socket (ServerSocket) which distributes
 * in a specific port. And then waits to hear some request
 * connection from any client.
 * Subsequently Server accepts the request. In order to receive
 * many requests, server must create every time a new Socket for each client.
 */
public class Server implements Runnable {
    private ServerSocket serverSocket;
    private int SERVER_PORT;
    private MainActivity context;

    public Server(MainActivity context, int port){
        this.SERVER_PORT = port;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(SERVER_PORT));
        }catch (BindException be){

        }catch (IOException e) {
            e.printStackTrace();
        }
        Socket clientSocket;
        try{
            while(!Thread.currentThread().isInterrupted()) {
                clientSocket = this.serverSocket.accept();
                clientSocket.setTcpNoDelay(true);

                ReadWrite readWrite = new ReadWrite(clientSocket, context);
                new Thread(readWrite).start();
                readWrites.add(readWrite); // holding the clients to send data later

                //while macAddress has been found
                while (true) {
                    if (hasSendMacAddress) {
                        initializeClients(clientSocket);
                        hasSendMacAddress = false;
                        break;
                    }
                }
            }
        }catch (NullPointerException ne){
            ne.printStackTrace();
        }catch (IOException e) {
            try{
                if(serverSocket != null){
                    serverSocket.close();
                }
            }catch (IOException io){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initializeClients(Socket clientSocket){
        ClientModel clientModel = new ClientModel();
        clientModel.setServerIp(clientSocket);
        clientModel.setClientPort(clientSocket);
        clientModel.setDeviceMacAddress(deviceArray, deviceMacAddresses);
        clientModel.setDeviceName(deviceArray, deviceMacAddresses);
        clientModel.setServerPort(clientSocket);
        clientModel.setClientIp(clientSocket);
        int clientPort = clientSocket.getPort();
        String [] clientIpAddress = clientSocket.getInetAddress().toString().split("/");
        mapClients.put(clientIpAddress[1], clientModel); //putting all the clients into a hash list
        clientCounter++;
        if(clientCounter == 1){
            information += "\n" + "Connected IP : " + clientIpAddress[1] + ":" + clientPort + "\n";
        }else if(clientCounter > 1){
            information += "Connected IP : " + clientIpAddress[1] + ":" + clientPort + "\n";
        }
        context.runOnUiThread(() -> updateTextView(information));
    }

    private void updateTextView(String message){
        TextView txtView = context.findViewById(R.id.connectionStatus2);
        txtView.setVisibility(View.VISIBLE);
        txtView.setText(message);
    }
}

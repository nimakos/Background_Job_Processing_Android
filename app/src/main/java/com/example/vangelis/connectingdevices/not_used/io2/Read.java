package com.example.vangelis.connectingdevices.not_used.io2;

import android.util.Log;

import com.example.vangelis.connectingdevices.model.ClientModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static com.example.vangelis.connectingdevices.utilities.Constants.deviceMacAddresses;
import static com.example.vangelis.connectingdevices.utilities.Constants.hasSendMacAddress;

public class Read implements Runnable {

    private Socket socket;
    private ObjectInputStream oi;

    public Read(Socket socket){
        this.socket = socket;
        try {
            oi = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        try {

            while(oi.available() > 0) {
                byte aek = oi.readByte();
                switch (aek) {
                    case 4:
                        try {
                            List<String> list = (List<String>) oi.readObject();
                            deviceMacAddresses.clear();
                            deviceMacAddresses.addAll(list);
                            hasSendMacAddress = true;
                            break;
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 5:
                        try {
                            Map<String, ClientModel> clientsFromServer1 = (Map<String, ClientModel>) oi.readObject();
                            Log.e("INFO", "Reading Done");

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

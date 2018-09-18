package com.example.vangelis.connectingdevices.utilities;

import android.net.wifi.p2p.WifiP2pDevice;

import com.example.vangelis.connectingdevices.io.ReadWrite;
import com.example.vangelis.connectingdevices.model.ClientModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
    public static final int CHAT_MESSAGE = 1;
    public static final int SEND_DEVICE_MAC_TO_SERVER = 2;
    public static final int SEND_THE_ARRAY_TO_CLIENTS_EFFICIENTLY = 3;
    public static final int SEND_ARRAY_TO_SERVER_EFFICIENTLY = 4;
    //------------------------------PARALLELISM-------------------------------------
    public static Map<String, ClientModel> mapClients = new HashMap<>();
    public static int result = 0;
    public static List<ReadWrite> readWrites = new ArrayList<>();
    public static List<String> deviceAddress = new ArrayList<>();
    public static String information = "Connected Devices:";
    public static int clientCounter = 0;
    public static boolean hasSendMacAddress = false;
    public static WifiP2pDevice[] deviceArray;
}

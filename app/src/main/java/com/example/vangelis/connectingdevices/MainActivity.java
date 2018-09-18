package com.example.vangelis.connectingdevices;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vangelis.connectingdevices.io.Client;
import com.example.vangelis.connectingdevices.io.ReadWrite;
import com.example.vangelis.connectingdevices.io.Server;
import com.example.vangelis.connectingdevices.mapping_services.MappingPrimes;
import com.example.vangelis.connectingdevices.model.ClientModel;
import com.example.vangelis.connectingdevices.network.WiFiDirectBroadcastReceiver;
import com.example.vangelis.connectingdevices.utilities.PrimeUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.vangelis.connectingdevices.utilities.Constants.CHAT_MESSAGE;
import static com.example.vangelis.connectingdevices.utilities.Constants.SEND_THE_ARRAY_TO_CLIENTS_EFFICIENTLY;

public class MainActivity extends AppCompatActivity {

    Button btnOnOff, btnDiscover, chatButton, dataMining;
    ListView listView;
    public TextView read_msg_box, connectionStatus, connectionStatus2;
    EditText writeMsg;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;  //For carrying data from one activity to another

    List<WifiP2pDevice> peers = new ArrayList<>();
    String[] deviceNameArray;
    public static WifiP2pDevice[] deviceArray;
    public static final int SERVER_PORT = 46528;

    //------------------------------PARALLELISM-------------------------------------
    public static Map<String, ClientModel> mapClients = new HashMap<>();
    public static int result = 0;
    public static List<ReadWrite> readWrites = new ArrayList<>();
    public static List<String> deviceAddress = new ArrayList<>();
    public static String information = "Connected Devices:";
    public static int clientCounter = 0;
    public static boolean hasSendMacAddress = false;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //----->not initially displaying the keyboard
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //-------> screen always on
        initialWork();
        exqListener();
        if(wifiManager.isWifiEnabled()){
            btnOnOff.setText("CLOSE WIFI");
        }else{
            btnOnOff.setText("OPEN WIFI");
        }
    }

    //the connected device from client side sends back the results(macAddress and deviceName) to its mainActivity
    BroadcastReceiver broadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            deviceAddress.clear();
            deviceAddress.add(intent.getStringExtra("address"));
        }
    };

    /**
     * The buttons of the main activity
     */
    @SuppressLint("SetTextI18n")
    private void exqListener() {
        btnOnOff.setOnClickListener(v -> {
            if(wifiManager.isWifiEnabled()){
                wifiManager.setWifiEnabled(false);
                btnOnOff.setText("OPEN WIFI");
            }else{
                wifiManager.setWifiEnabled(true);
                btnOnOff.setText("CLOSE WIFI");
            }
        });

        //(discover Peers)
        btnDiscover.setOnClickListener(v -> mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connectionStatus.setText("Discovery Started");
            }

            @Override
            public void onFailure(int reason) {
                connectionStatus.setText("Discovery Failed");
            }
        }));

        //Connection with the chosen device from the list using mChannel (Client Side)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            final WifiP2pDevice device = deviceArray[position];
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress; //αποθηκευουμε στο config την διέυθυνση (ip) της εκάστοτε συσκεύης

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getApplicationContext(), device.deviceName + " disconnected", Toast.LENGTH_SHORT).show();
                }
            });
        });

        //chat Messages
        chatButton.setOnClickListener(v -> {
            String msg = writeMsg.getText().toString();
            try{
                new Thread(() -> {
                    for(ReadWrite i : readWrites){
                        i.writeObject(CHAT_MESSAGE, msg);
                    }
                }).start();
                writeMsg.setText("");
            }catch (Exception e){
                e.printStackTrace();
                final String eString = e.toString();
                Toast.makeText(getApplicationContext(), eString,Toast.LENGTH_LONG).show();
            }
        });

        //do the calculations
        dataMining.setOnClickListener(view -> executePrimeNumbers());
    }

    private void executePrimeNumbers(){
        try {
            new Thread(() -> {
                //int[] createPrimeArray = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
                int [] createPrimeArray = PrimeUtils.randomIntegers(20_000_000);
                long startTime = System.nanoTime();
                mapClients = MappingPrimes.putPrimeArray(mapClients, createPrimeArray);
                mapClients = MappingPrimes.putTime(mapClients, startTime);
                for (ReadWrite i : readWrites) {
                    try {
                        for (Map.Entry<String, ClientModel> pair : mapClients.entrySet()){
                            if(pair.getKey().equals(i.clientSocket.getInetAddress().getHostAddress())){
                                i.write(SEND_THE_ARRAY_TO_CLIENTS_EFFICIENTLY, pair.getValue().getPrimeChunkedArray());
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*private void executeSumNumbers(){
        new Thread(() -> {
            //double[] createArray = {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0};
            double[]  createArray = SumUtils.randomDoubles(100);
            long startTime = System.nanoTime();
            mapClients = MappingSum.putArray(mapClients, createArray);
            mapClients = MappingSum.putTime(mapClients, startTime);
            for (ReadWrite i : readWrites) {
                try{
                    for (Map.Entry<String, ClientModel> pair : mapClients.entrySet()){
                        if(pair.getKey().equals(i.clientSocket.getInetAddress().getHostAddress())){
                            i.write(SEND_THE_ARRAY_TO_CLIENTS_EFFICIENTLY, pair.getValue().getChunkedArray());
                            break;
                        }
                    }
                }catch (IOException io){
                    io.printStackTrace();
                }
            }
        }).start();
    }*/

    private void initialWork() {
        btnOnOff = findViewById(R.id.onOff);
        btnDiscover = findViewById(R.id.discover);
        chatButton = findViewById(R.id.sendButton);
        listView = findViewById(R.id.peerListView);
        read_msg_box = findViewById(R.id.readMsg);
        connectionStatus = findViewById(R.id.connectionStatus);
        connectionStatus2 = findViewById(R.id.connectionStatus2);
        dataMining = findViewById(R.id.doSomething);
        writeMsg = findViewById(R.id.writeMsg);

        //This class provides the API for managing WI-FI connectivity
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //This class provides the API for managing WI-FI peer-to-peer connectivity
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        //A channel that connects the application to the WI-FI p2p framework
        mChannel = mManager.initialize(this, getMainLooper(), null);
        //create an object from the class i made
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        //Create Intent to pass the parameters to WifiDirectBroadcastReceiver class(Activity)
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.MAIN"));
    }

    /**
     * View peers
     * (Peer List)
     */
    public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;

                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName + " - " + device.deviceAddress; //save only name and address in array of Strings
                    deviceArray[index] = device; //save all the objects (devices) with their characteristics in array type of WifiP2pDeviceList
                    index++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                listView.setAdapter(adapter);
            }

            if (peers.size() == 0) {
                Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
                mapClients.clear();
                readWrites.clear();
                connectionStatus.setText("");
                connectionStatus2.setText("");
                information = "Connected Devices:";
                clientCounter = 0;
                result = 0;
                hasSendMacAddress = false;
                return;
            }
            deletePeersOnDisconnection();
        }
    };

    /**
     * Checking who is the Host and who is the client
     * There has been the connection and we take information
     * Initialize and start the Server
     * Initialize and start the Client
     */
    public WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            //find the ip address of the server
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

                //Server side
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                dataMining.setVisibility(View.VISIBLE);
                connectionStatus.setText("I am the Server");
                new Thread((new Server(MainActivity.this, SERVER_PORT))).start();

                //client side
            }else if(wifiP2pInfo.groupFormed){
                connectionStatus.setText("I am the Client");
                new Thread(new Client(groupOwnerAddress, SERVER_PORT, MainActivity.this)).start();
            }
        }
    };

    /**
     * Compare two different tables and find
     * witch device has been of the network
     * Because of the api this could take and 1 minute
     * to final discover the device that has been off the network
     */
    private void deletePeersOnDisconnection(){
        try {
            int counter;
            if (mapClients.size() > deviceArray.length && deviceArray.length != 0) {
                Iterator<Map.Entry<String, ClientModel>> mapClientIterator = mapClients.entrySet().iterator();
                outer:
                while(mapClientIterator.hasNext()){
                    Map.Entry<String, ClientModel> i = mapClientIterator.next();
                    counter = 0;
                    for (WifiP2pDevice device : deviceArray) {
                        if (i.getValue().getDeviceMacAddress() != null) {
                            if (!i.getValue().getDeviceMacAddress().equals(device.deviceAddress)) {
                                counter++; //εαν έχουν μετρηθεί οι συσκεύες και δεν έχει βρεθεί η συσκεύη στην κανονική λίστα τότε διεγραψέ την και από το προσωρινό μας HasList
                                if (counter == deviceArray.length) { //εαν ισχυει η συσκεύη δεν βρεθηκε
                                    mapClientIterator.remove(); //και θα διαγραφει απο την λίστα μας
                                    Iterator<ReadWrite> readWriteIterator = readWrites.iterator();
                                    while(readWriteIterator.hasNext()){
                                        ReadWrite k = readWriteIterator.next();
                                        if(k.clientSocket.getInetAddress().getHostAddress().equals(i.getValue().getClientIp())){
                                            readWriteIterator.remove();
                                            result = 0;
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch (ConcurrentModificationException ce){
            ce.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.MAIN"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        unregisterReceiver(broadcastReceiver);
        mManager.removeGroup(mChannel, null);
    }
}
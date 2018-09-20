package com.example.vangelis.connectingdevices.io;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vangelis.connectingdevices.MainActivity;
import com.example.vangelis.connectingdevices.R;
import com.example.vangelis.connectingdevices.mapping_services.MappingPrimes;
import com.example.vangelis.connectingdevices.mapping_services.messages_from_to_service.Message;
import com.example.vangelis.connectingdevices.mapping_services.messages_from_to_service.MessageReceiverFromService;
import com.example.vangelis.connectingdevices.model.ClientModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.vangelis.connectingdevices.utilities.Constants.deviceAddress;
import static com.example.vangelis.connectingdevices.utilities.Constants.hasSendMacAddress;
import static com.example.vangelis.connectingdevices.utilities.Constants.mapClients;
import static com.example.vangelis.connectingdevices.utilities.Constants.readWrites;
import static com.example.vangelis.connectingdevices.utilities.Constants.result;
import static com.example.vangelis.connectingdevices.utilities.Constants.CHAT_MESSAGE;
import static com.example.vangelis.connectingdevices.utilities.Constants.SEND_RESULT_BACK_TO_SERVER;
import static com.example.vangelis.connectingdevices.utilities.Constants.SEND_DEVICE_MAC_TO_SERVER;
import static com.example.vangelis.connectingdevices.utilities.Constants.SEND_THE_ARRAY_TO_CLIENTS;

/**
 * Socket is the endpoint of a two way communication link
 * between two processes running on the network.
 * It is identified by the combination of an IP address and a port number
 *
 * Here we do the read and the write withe the socket streams
 */
public class ReadWrite implements Runnable {
    public Socket clientSocket ;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;
    private MainActivity context;

    ReadWrite(Socket skt, MainActivity context){
        this.context = context;
        this.clientSocket = skt;
        try {
            clientSocket.setTcpNoDelay(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
             objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
             objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()){
                byte messageType = objectInputStream.readByte();
                switch (messageType) {
                    case CHAT_MESSAGE:  //------------------->Both Sides
                        String i = (String) objectInputStream.readObject();
                        context.runOnUiThread(() -> updateTextView(i));
                        break;
                    case SEND_DEVICE_MAC_TO_SERVER: //---------->server side
                        List<String> list = (List<String>) objectInputStream.readObject();
                        deviceAddress.clear();
                        deviceAddress.addAll(list);
                        hasSendMacAddress = true;
                        break;
                    case SEND_THE_ARRAY_TO_CLIENTS: //--------->clients side
                        String newIpFromClient = clientSocket.getLocalAddress().getHostName();
                        int arraySize = objectInputStream.readInt();
                        int [] intArray = new int[arraySize];
                        for(int k = 0; k < arraySize; k++){
                            intArray[k] = objectInputStream.readInt();
                        }
                        String kindOfCalculation = objectInputStream.readUTF();
                        MessageReceiverFromService newReceiver = new MessageReceiverFromService(new Message(newIpFromClient,readWrites));
                        MappingPrimes.arrayToCalculate = intArray;
                        Intent intent = new Intent(context, MappingPrimes.class);
                        intent.putExtra("receiver", newReceiver);
                        intent.putExtra("kindOfCalculation", kindOfCalculation);
                        context.startService(intent);
                        break;
                    case SEND_RESULT_BACK_TO_SERVER: //------------>server side
                        String newClientIp = objectInputStream.readUTF();
                        long newResult = objectInputStream.readLong();
                        finalSetOfMapForPrimes(mapClients, newClientIp, newResult);
                        break;
                }
            }
        }catch (NullPointerException np){
            np.printStackTrace();
        }catch (IOException | ClassNotFoundException e){
            try{
                if(objectInputStream != null)
                    objectInputStream.close();
            }catch (IOException io){
                io.printStackTrace();
            }
            try{
                if(objectOutputStream != null)
                    objectOutputStream.close();
            }catch (IOException io){
                io.printStackTrace();
            }
            try{
                if(clientSocket != null){
                    clientSocket.close();
                }
            }catch (IOException io){
                io.printStackTrace();
            }
        }
    }

    public void writeObject(int b, Object obj){
        try {
            objectOutputStream.writeByte(b);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePrimes(int jobToDo, int[] data, String kindOfCalculation) throws IOException
    {
        objectOutputStream.writeByte(jobToDo);
        objectOutputStream.writeInt(data.length);
        for (int aData : data) {
            objectOutputStream.writeInt(aData);
        }
        objectOutputStream.writeUTF(kindOfCalculation);
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    public void writeStringPrimes(int jobToDo, String ip, long result) throws IOException
    {
        objectOutputStream.writeByte(jobToDo);
        objectOutputStream.writeUTF(ip);
        objectOutputStream.writeLong(result);
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    public void writeDoubles(int jobToDo, double[] data, String kindOfCalculation) throws IOException
    {
        objectOutputStream.writeByte(jobToDo);
        objectOutputStream.writeInt(data.length);
        for (double aData : data) {
            objectOutputStream.writeDouble(aData);
        }
        objectOutputStream.writeUTF(kindOfCalculation);
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    public void writeStringDoubles(int jobToDo, String ip, double result) throws IOException
    {
        objectOutputStream.writeByte(jobToDo);
        objectOutputStream.writeUTF(ip);
        objectOutputStream.writeDouble(result);
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    private void updateTextView(String message){
        TextView txtView = context.findViewById(R.id.readMsg);
        txtView.setText(message);
    }

    private void updateToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void finalSetOfMapForPrimes(Map<String, ClientModel> mp, String ip, long finalResult){
        for (Map.Entry<String, ClientModel> pair : mp.entrySet()) {
            if (pair.getKey().equals(ip)) {
                pair.getValue().setPrimeResultFromSumArray(finalResult);
                pair.getValue().setHasCompleteTheJob(true);
                long endTime = System.nanoTime();
                pair.getValue().setEndTime(endTime); //measure hole time
                String resultMessage = String.format(Locale.ENGLISH, "Elapsed time for %s : %.3f seconds.%n", pair.getValue().getDeviceName() ,pair.getValue().getElapsedSeconds());
                Log.e("TOTAL TIME : ", resultMessage);

                result += pair.getValue().getPrimeResultFromSumArray();
                String deviceName = pair.getValue().getDeviceName();
                double deviceTime = pair.getValue().getElapsedSeconds();
                String finalMessage = "Device " + deviceName + " Has done " + String.valueOf(deviceTime) + " Sec " + " the result is " + String.valueOf(result);
                context.runOnUiThread(() -> updateToast(finalMessage));
                break;
            }
        }
    }
}

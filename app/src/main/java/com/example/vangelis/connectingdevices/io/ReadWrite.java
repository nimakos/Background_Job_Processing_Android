package com.example.vangelis.connectingdevices.io;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vangelis.connectingdevices.MainActivity;
import com.example.vangelis.connectingdevices.R;
import com.example.vangelis.connectingdevices.mapping_services.MappingPrimes;
import com.example.vangelis.connectingdevices.mapping_services.MappingSum;
import com.example.vangelis.connectingdevices.mapping_services.messages_from_to_service.Message;
import com.example.vangelis.connectingdevices.mapping_services.messages_from_to_service.MessageReceiverFromService;
import com.example.vangelis.connectingdevices.model.ClientModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.vangelis.connectingdevices.constans.Constants.CHAT_MESSAGE;
import static com.example.vangelis.connectingdevices.constans.Constants.COUNTER_PEERS;
import static com.example.vangelis.connectingdevices.constans.Constants.PRIME;
import static com.example.vangelis.connectingdevices.constans.Constants.SEND_DEVICE_MAC_TO_SERVER;
import static com.example.vangelis.connectingdevices.constans.Constants.SEND_RESULT_BACK_TO_SERVER;
import static com.example.vangelis.connectingdevices.constans.Constants.SEND_THE_ARRAY_TO_CLIENTS;
import static com.example.vangelis.connectingdevices.constans.Constants.SUM;
import static com.example.vangelis.connectingdevices.constans.Constants.deviceMacAddresses;
import static com.example.vangelis.connectingdevices.constans.Constants.hasSendMacAddress;
import static com.example.vangelis.connectingdevices.constans.Constants.mapClients;
import static com.example.vangelis.connectingdevices.constans.Constants.readWrites;
import static com.example.vangelis.connectingdevices.constans.Constants.result;

/**
 * Socket is the endpoint of a two way communication link
 * between two processes running on the network.
 * It is identified by the combination of an IP address and a port number
 * <p>
 * Here we do the read and the write withe the socket streams
 */
public class ReadWrite implements Runnable {
    public Socket clientSocket;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;
    private MainActivity context;

    ReadWrite(Socket skt, MainActivity context) {
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
            while (!Thread.currentThread().isInterrupted()) {
                byte messageType = objectInputStream.readByte();
                switch (messageType) {
                    case CHAT_MESSAGE: { //------------------->Both Sides
                        String message = (String) objectInputStream.readObject();
                        context.runOnUiThread(() -> updateTextView(message));
                        break;
                    }
                    case SEND_DEVICE_MAC_TO_SERVER: { //---------->server side
                        List<String> list = (List<String>) objectInputStream.readObject();
                        deviceMacAddresses.clear();
                        deviceMacAddresses.addAll(list);
                        hasSendMacAddress = true;
                        break;
                    }
                    case SEND_THE_ARRAY_TO_CLIENTS: { //--------->clients side
                        String clientIp = clientSocket.getLocalAddress().getHostName();
                        String kindOfAlgorithm = objectInputStream.readUTF();
                        switch (kindOfAlgorithm) {
                            case "Primes": {
                                int arraySize = objectInputStream.readInt();
                                int[] intArray = new int[arraySize];
                                for (int k = 0; k < arraySize; k++) {
                                    intArray[k] = objectInputStream.readInt();
                                }
                                String kindOfCalculation = objectInputStream.readUTF();
                                MessageReceiverFromService newReceiver = new MessageReceiverFromService(new Message(clientIp, readWrites, context));
                                MappingPrimes.arrayToCalculate = intArray;
                                Intent intent = new Intent(context, MappingPrimes.class);
                                intent.putExtra("receiver", newReceiver);
                                intent.putExtra("kindOfCalculation", kindOfCalculation);
                                context.startService(intent);
                                break;
                            }
                            case "Doubles": {
                                int arraySize = objectInputStream.readInt();
                                double[] doubleArray = new double[arraySize];
                                for (int k = 0; k < arraySize; k++) {
                                    doubleArray[k] = objectInputStream.readDouble();
                                }
                                String kindOfCalculation = objectInputStream.readUTF();
                                MessageReceiverFromService newReceiver = new MessageReceiverFromService(new Message(clientIp, readWrites, context));
                                MappingSum.arrayToCalculate = doubleArray;
                                Intent intent = new Intent(context, MappingSum.class);
                                intent.putExtra("receiver", newReceiver);
                                intent.putExtra("kindOfCalculation", kindOfCalculation);
                                context.startService(intent);
                                break;
                            }
                        }
                        break;
                    }
                    case SEND_RESULT_BACK_TO_SERVER: { //------------>server side
                        COUNTER_PEERS++;
                        String clientIp = objectInputStream.readUTF();
                        int some = objectInputStream.readInt();
                        switch (some) {
                            case PRIME: {
                                long primeResult = objectInputStream.readLong();
                                Map<String, ClientModel> map = finalSetOfMapForPrimes(mapClients, clientIp, primeResult);
                                if (COUNTER_PEERS == mapClients.size()) {
                                    finalPrimeMessage(map);
                                    COUNTER_PEERS = 0;
                                }
                                break;
                            }
                            case SUM: {
                                double doubleResult = objectInputStream.readDouble();
                                Map<String, ClientModel> map = finalSetOfMapForDoubles(mapClients, clientIp, doubleResult);
                                if (COUNTER_PEERS == mapClients.size()) {
                                    finalDoubleMessage(map);
                                    COUNTER_PEERS = 0;
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        } catch (NullPointerException np) {
            np.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            try {
                if (objectInputStream != null)
                    objectInputStream.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
            try {
                if (objectOutputStream != null)
                    objectOutputStream.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    public void writeObject(int b, Object obj) {
        try {
            objectOutputStream.writeByte(b);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePrimes(int jobToDo, int[] data, String kindOfCalculation, String kindOfAlgorithm) throws IOException {
        objectOutputStream.writeByte(jobToDo);
        objectOutputStream.writeUTF(kindOfAlgorithm);
        objectOutputStream.writeInt(data.length);
        for (int aData : data) {
            objectOutputStream.writeInt(aData);
        }
        objectOutputStream.writeUTF(kindOfCalculation);
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    public void writeStringPrimes(int jobToDo, String ip, long result, int isPrime) throws IOException {
        objectOutputStream.writeByte(jobToDo);
        objectOutputStream.writeUTF(ip);
        objectOutputStream.writeInt(isPrime);
        objectOutputStream.writeLong(result);
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    public void writeDoubles(int jobToDo, double[] data, String kindOfCalculation, String kindOfAlgorithm) throws IOException {
        objectOutputStream.writeByte(jobToDo);
        objectOutputStream.writeUTF(kindOfAlgorithm);
        objectOutputStream.writeInt(data.length);
        for (double aData : data) {
            objectOutputStream.writeDouble(aData);
        }
        objectOutputStream.writeUTF(kindOfCalculation);
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    public void writeStringDoubles(int jobToDo, String ip, double result, int isSum) throws IOException {
        objectOutputStream.writeByte(jobToDo);
        objectOutputStream.writeUTF(ip);
        objectOutputStream.writeInt(isSum);
        objectOutputStream.writeDouble(result);
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    private void updateTextView(String message) {
        TextView txtView = context.findViewById(R.id.readMsg);
        txtView.setText(message);
    }

    private void updateToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Update final table using Prime Algorithm
     *
     * @param mp          Initial Table
     * @param ip          Client Ip
     * @param finalResult Result of Primes
     * @return Final updated Table
     */
    private Map<String, ClientModel> finalSetOfMapForPrimes(Map<String, ClientModel> mp, String ip, long finalResult) {
        for (Map.Entry<String, ClientModel> pair : mp.entrySet()) {
            if (pair.getKey().equals(ip)) {
                pair.getValue().setPrimeResultFromSumArray(finalResult);
                pair.getValue().setHasCompleteTheJob(true);
                long endTime = System.nanoTime();
                pair.getValue().setEndTime(endTime); //measure hole time
                String resultMessage = String.format(Locale.ENGLISH, "Elapsed time for %s : %.3f seconds.%n", pair.getValue().getDeviceName(), pair.getValue().getElapsedSeconds());
                Log.e("TOTAL : ", resultMessage);

                result += pair.getValue().getPrimeResultFromSumArray();
                String deviceName = pair.getValue().getDeviceName();
                double deviceTime = pair.getValue().getElapsedSeconds();
                String finalMessage = "Device " + deviceName + " Has done " + String.valueOf(BigDecimal.valueOf(deviceTime).setScale(3, BigDecimal.ROUND_HALF_UP)) + " Sec " + " the result is " + String.valueOf(result);
                context.runOnUiThread(() -> updateToast(finalMessage));
                break;
            }
        }
        return mp;
    }

    /**
     * Update final table using Sum Algorithm
     * and show Toast message
     * @param mp          Initial Table
     * @param ip          Client Ip
     * @param finalResult Result of doubles
     * @return Final updated Table
     */
    private Map<String, ClientModel> finalSetOfMapForDoubles(Map<String, ClientModel> mp, String ip, double finalResult) {
        for (Map.Entry<String, ClientModel> pair : mp.entrySet()) {
            if (pair.getKey().equals(ip)) {
                pair.getValue().setResultFromSumArray(finalResult);
                pair.getValue().setHasCompleteTheJob(true);
                long endTime = System.nanoTime();
                pair.getValue().setEndTime(endTime); //measure hole time
                String resultMessage = String.format(Locale.ENGLISH, "Elapsed time for %s : %.3f seconds.%n", pair.getValue().getDeviceName(), pair.getValue().getElapsedSeconds());
                Log.e("TOTAL : ", resultMessage);

                result += pair.getValue().getResultFromSumArray();
                String deviceName = pair.getValue().getDeviceName();
                double deviceTime = pair.getValue().getElapsedSeconds();
                String finalMessage = "Device " + deviceName + " Has done " + String.valueOf(BigDecimal.valueOf(deviceTime).setScale(3, BigDecimal.ROUND_HALF_UP)) + " Sec " + " the result is " + String.valueOf(result);
                context.runOnUiThread(() -> updateToast(finalMessage));
                break;
            }
        }
        return mp;
    }

    /**
     * Loop in the Final table and show the results (Of Primes) in the UI
     * and show Toast message
     * @param mp Final table
     */
    private void finalPrimeMessage(Map<String, ClientModel> mp) {
        outer:
        while (true) {
            double maxTime = 0.0;
            long result = 0;
            int count = 0;
            for (Map.Entry<String, ClientModel> pair : mp.entrySet()) {
                if (pair.getValue().getHasCompleteTheJob()) {
                    count++;
                    if (pair.getValue().getElapsedSeconds() > maxTime) {
                        maxTime = pair.getValue().getElapsedSeconds();
                    }
                    result += pair.getValue().getPrimeResultFromSumArray();
                    if (mp.size() == count) {
                        String finalMessage = String.format(Locale.ENGLISH, "Total time taken calculating an array of integers is %.3f seconds. \n And found %d prime numbers", maxTime, result);
                        notification(finalMessage);
                        for (Map.Entry<String, ClientModel> pair1 : mp.entrySet()) {
                            if (pair1.getValue().getHasCompleteTheJob()) {
                                pair1.getValue().setElapsedSeconds(0.0);
                                pair1.getValue().setPrimeResultFromSumArray(0);
                            }
                        }
                        break outer;
                    }
                }
            }
        }
    }

    /**
     * Loop in the Final table and show the results (Of Sums) in the UI
     * and show notification message
     * @param mp Final table
     */
    private void finalDoubleMessage(Map<String, ClientModel> mp) {
        outer:
        while (true) {
            double maxTime = 0.0;
            double result = 0.0;
            int count = 0;
            for (Map.Entry<String, ClientModel> pair : mp.entrySet()) {
                if (pair.getValue().getHasCompleteTheJob()) {
                    count++;
                    if (pair.getValue().getElapsedSeconds() > maxTime) {
                        maxTime = pair.getValue().getElapsedSeconds();
                    }
                    result += pair.getValue().getResultFromSumArray();
                    if (mp.size() == count) {
                        String finalMessage = String.format(Locale.ENGLISH, "Total time taken calculating the sum of an array of doubles is %.3f seconds. \n And the sum result is %.3f", maxTime, result);
                        notification(finalMessage);
                        for (Map.Entry<String, ClientModel> pair1 : mp.entrySet()) {
                            if (pair1.getValue().getHasCompleteTheJob()) {
                                pair1.getValue().setElapsedSeconds(0.0);
                                pair1.getValue().setResultFromSumArray(0.0);
                            }
                        }
                        break outer;
                    }
                }
            }
        }
    }

    /**
     * The notifications Message
     *
     * @param finalMessage The Message to show
     */
    private void notification(String finalMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Final Results")
                .setMessage(finalMessage)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        context.runOnUiThread(alertDialogBuilder::create);
        context.runOnUiThread(alertDialogBuilder::show);
    }
}

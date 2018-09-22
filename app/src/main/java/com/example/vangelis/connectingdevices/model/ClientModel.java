package com.example.vangelis.connectingdevices.model;

import android.net.wifi.p2p.WifiP2pDevice;

import java.io.Serializable;
import java.net.Socket;
import java.util.List;

public class ClientModel implements Serializable {

    private String serverIp, deviceName, deviceMacAddress, clientIp;
    private int serverPort, clientPort;
    private double resultFromSumArray;
    private double[] chunkedArray;
    private boolean hasCompleteTheJob;
    private long startTime, endTime;
    private static final double ONE_BILLION = 1_000_000_000;
    private double elapsedSeconds;
    private int[] primeChunkedArray;
    private long primeResultFromSumArray;

    public ClientModel() {
    }

    public double getElapsedSeconds() {
        return this.elapsedSeconds;
    }

    public void setElapsedSeconds(double elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
        this.elapsedSeconds = (endTime - this.startTime) / ONE_BILLION;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(Socket socket) {
        String[] clientIpAddress = socket.getInetAddress().toString().split("/");
        this.clientIp = clientIpAddress[1];
    }

    public boolean getHasCompleteTheJob() {
        return hasCompleteTheJob;
    }

    public void setHasCompleteTheJob(boolean hasCompleteTheJob) {
        this.hasCompleteTheJob = hasCompleteTheJob;
    }


    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(Socket socket) {
        this.serverIp = socket.getLocalAddress().getHostName();
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(WifiP2pDevice[] wifiP2pDevice, List<String> deviceAddress) {
        //απο την mac address βρισκω το ονομα του κινητου και αρα ποιο κινητο εκανε σύνδεση
        for (WifiP2pDevice j : wifiP2pDevice) {
            for (String i : deviceAddress) {
                if (j.deviceAddress.equals(i)) { //εαν ταυτίζονται πάει να πεί οτι η συγκεκριμένη συσκεύη έχει κάνει connection
                    this.deviceName = j.deviceName;
                    break;
                }
            }
        }
    }

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }

    public void setDeviceMacAddress(WifiP2pDevice[] wifiP2pDevice, List<String> deviceAddress) {
        for (WifiP2pDevice j : wifiP2pDevice) {
            for (String i : deviceAddress) {
                if (j.deviceAddress.equals(i)) { //εαν ταυτίζονται πάει να πεί οτι η συγκεκριμένη συσκεύη έχει κάνει connection
                    this.deviceMacAddress = j.deviceAddress;
                    //Log.e("Mac Address", this.deviceMacAddress);
                    break;
                }
            }
        }
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(Socket socket) {
        this.serverPort = socket.getLocalPort();
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(Socket socket) {
        this.clientPort = socket.getPort();
    }

    public double getResultFromSumArray() {
        return resultFromSumArray;
    }

    public void setResultFromSumArray(double resultFromSumArray) {
        this.resultFromSumArray = resultFromSumArray;
    }

    public double[] getChunkedArray() {
        return chunkedArray;
    }

    public void setChunkedArray(double[] chunkedArray) {
        this.chunkedArray = chunkedArray;
    }

    public int[] getPrimeChunkedArray() {
        return primeChunkedArray;
    }

    public void setPrimeChunkedArray(int[] primeChunkedArray) {
        this.primeChunkedArray = primeChunkedArray;
    }

    public long getPrimeResultFromSumArray() {
        return primeResultFromSumArray;
    }

    public void setPrimeResultFromSumArray(long primeResultFromSumArray) {
        this.primeResultFromSumArray = primeResultFromSumArray;
    }
}

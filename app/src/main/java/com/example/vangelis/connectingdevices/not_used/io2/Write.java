package com.example.vangelis.connectingdevices.not_used.io2;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Write implements Runnable {
    private Socket socket;
    private Object obj;
    private int bytes;
    ObjectOutputStream oos;
    OutputStream os;
    private byte [] data;

    public Write(Socket skt, int bytes ,Object obj){

        this.socket = skt;
        this.obj = obj;
        this.bytes = bytes;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Write(Socket skt, int bytes ,Object obj, byte [] data){
        this.data = data;
        this.socket = skt;
        this.obj = obj;
        this.bytes = bytes;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
       writeTCP();

    }

    public void writeTCP(){
        try{
            oos.writeByte(bytes);
            oos.writeObject(obj);
            oos.flush();
            oos.reset();
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    public void sendDataTCP() throws IOException
    {
        os.write(this.data, 0, this.data.length);
        os.flush();
    }
}

package com.example.vangelis.connectingdevices.not_used.nio;

import com.example.vangelis.connectingdevices.model.ClientModel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Client implements Runnable {

    private String hostAddress;
    private int serverPort;
    private SocketChannel clientSocketChannel;
    List<ClientModel> clientModels = new ArrayList<>();

    public Client(InetAddress hostAddress, int serverPort){
        this.hostAddress = hostAddress.getHostAddress();
        this.serverPort = serverPort;
    }


    @Override
    public void run() {
        try {
            clientSocketChannel = SocketChannel.open();
            clientSocketChannel.configureBlocking(false);
            clientSocketChannel.connect(new InetSocketAddress(this.hostAddress, this.serverPort));
            while(! clientSocketChannel.finishConnect() ){
                //wait, or do something else...
            }

        /*    ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream (baos);
            oos.writeObject(clientModels);
            oos.flush();
            clientSocketChannel.write(ByteBuffer.wrap(baos.toByteArray()));*/

            // Send messages to server
            String  messages = "Time goes fast";


            ByteBuffer buf = ByteBuffer.allocate(48);
            buf.clear();
            buf.put(messages.getBytes());
            buf.flip();
            while(buf.hasRemaining()){
                clientSocketChannel.write(buf);
            }

            /*String [] messages = new String [] {"Time goes fast.", "What now?", "Bye."};

            for (int i = 0; i < messages.length; i++) {

                byte [] message = messages[i].getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(message);
                clientSocketChannel.write(buffer);

                System.out.println(messages [i]);
                buffer.clear();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (clientSocketChannel != null) {
                clientSocketChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

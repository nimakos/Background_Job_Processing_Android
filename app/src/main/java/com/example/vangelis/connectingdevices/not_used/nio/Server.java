package com.example.vangelis.connectingdevices.not_used.nio;

import android.os.Build;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server implements Runnable {

    private ServerSocketChannel serverSocketChannel;
    private int serverPort;
    private Selector selector;

    public Server(int serverPort){
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try {
            this.selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            // retrieve server socket and bind to port
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                serverSocketChannel.bind(new InetSocketAddress(serverPort));
                int ops = serverSocketChannel.validOps();
                serverSocketChannel.register(selector, ops, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        while(true){
            try {
                this.selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while(iterator.hasNext()){
                    SelectionKey myKey = iterator.next();

                    if(myKey.isAcceptable()){
                        SocketChannel clientSocketChannel = this.serverSocketChannel.accept();
                        clientSocketChannel.configureBlocking(false);
                        clientSocketChannel.register(selector, SelectionKey.OP_READ);
                    }else if (myKey.isReadable()){
                        SocketChannel clientSocketChannel = (SocketChannel) myKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                        clientSocketChannel.read(byteBuffer);
                        String result = new String(byteBuffer.array()).trim();
                        System.out.println(result);
                    }
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}

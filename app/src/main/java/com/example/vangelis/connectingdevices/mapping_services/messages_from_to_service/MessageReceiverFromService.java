package com.example.vangelis.connectingdevices.mapping_services.messages_from_to_service;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

@SuppressLint("ParcelCreator")
public class MessageReceiverFromService extends ResultReceiver {

    private Message message;

    public MessageReceiverFromService(Message message) {
        super(new Handler(Looper.getMainLooper()));

        this.message = message;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        message.displayMessage(resultCode, resultData);
    }
}

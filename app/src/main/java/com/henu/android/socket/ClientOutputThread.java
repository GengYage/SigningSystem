package com.henu.android.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class ClientOutputThread implements Runnable {

    private Socket mSocket;
    private OutputStream outputStream;
    public Handler sendHandler;

    public ClientOutputThread(Socket socket) {
        this.mSocket = socket;
        try {

            outputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        Looper.prepare();

        sendHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if(msg.what == 1){
                    String content = msg.obj.toString();
                    try {
                        outputStream.write(content.getBytes("UTF-8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Looper.loop();

    }


}

package com.henu.android.socket;


import android.os.Handler;
import android.os.Message;

import com.henu.android.entity.News;
import com.henu.android.utils.JSONUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ClientInputThread implements Runnable {

    private Socket mSocket;
    private BufferedReader mReader;
    private Handler receiveHandler;

    public ClientInputThread(Socket mSocket) {
        this.mSocket = mSocket;
        try {
            mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(),"UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHandler(Handler handler){
        this.receiveHandler = handler;
    }


    @Override
    public void run() {
        String content = null;
        char[] buffer = new char[1024];
        int len = 0;

        try {
              while (((len = mReader.read(buffer,0,1024))!=-1)){
                  content = String.valueOf(buffer,0,len);
                  News news = JSONUtils.JsonToMessage(content);
                  Message msg = new Message();
                  msg.what = 0;
                  msg.obj = content;
                  receiveHandler.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                mReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

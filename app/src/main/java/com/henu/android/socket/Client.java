package com.henu.android.socket;

import android.os.Handler;


public class Client  {
    private String hostname = "120.55.194.26";
    private int port = 8989;
    private ClientThread clientThread;
    private Handler mHandler;

    public Client(Handler mHandler){
        this.mHandler = mHandler;
    }

    public void start(){
                System.out.println("Connected...");
                clientThread = new ClientThread(hostname,port,mHandler);
                new Thread(clientThread).start();
    }

    public ClientInputThread getClientInputThread(){
        return clientThread.getIn();
    }

    public ClientOutputThread getClientOutputThread(){
        return clientThread.getOut();
    }

}

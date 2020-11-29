package com.henu.android.socket;

import android.os.Handler;

import java.io.IOException;
import java.net.Socket;


public class ClientThread  implements  Runnable{

    private String hostname;
    private int port;
    private Socket mSocket;
    private ClientInputThread in;
    private ClientOutputThread out;
    private Handler receiveHandler;

    public ClientThread(String hostname, int port, Handler handler) {
        this.hostname = hostname;
        this.port = port;
        receiveHandler = handler;
    }

    @Override
    public void run() {
        try {
            mSocket = new Socket(hostname,port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        in = new ClientInputThread(mSocket);
        in.setHandler(receiveHandler);
        out = new ClientOutputThread(mSocket);
        new Thread(in).start();
        new Thread(out).start();

    }

    public ClientInputThread getIn() {
        return in;
    }


    public ClientOutputThread getOut() {
        return out;
    }

}

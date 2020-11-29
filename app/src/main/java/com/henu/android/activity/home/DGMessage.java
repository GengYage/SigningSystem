package com.henu.android.activity.home;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.henu.android.activity.R;
import com.henu.android.entity.News;

import java.util.ArrayList;

public class DGMessage extends Fragment {

    //定义接口用于和Activity通信
    public interface OnSendMsg {
        void sendMsg(String context);
        MessageAdapter getMessageAdapter();
        ArrayList<News> getMessage();
    }

    private OnSendMsg onSendMsg;
    private static ListView listView;
    private static ArrayList<News> myMessages;
    private static MessageAdapter messageAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onSendMsg = (OnSendMsg)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"must implements OnSendMsg");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dgmessage,container,false);

        Button msgSendBtn = view.findViewById(R.id.msg_send_btn);
        EditText edit =  view.findViewById(R.id.msg_edit);
        listView = view.findViewById(R.id.msgs);
        myMessages = onSendMsg.getMessage();
        //刷新一次消息
        messageAdapter = onSendMsg.getMessageAdapter();
        listView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
        listView.setSelection(myMessages.size());
        //发送新的消息
        msgSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendMsg.sendMsg(edit.getText().toString());
                messageAdapter = onSendMsg.getMessageAdapter();
                listView.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();

                edit.setText("");
                listView.setSelection(myMessages.size());
            }
        });
        return view;
    }

    public static Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 3:
                    messageAdapter.notifyDataSetChanged();
                    listView.setSelection(myMessages.size());
                    break;
                default:
                    break;
            }
        }
    };
}

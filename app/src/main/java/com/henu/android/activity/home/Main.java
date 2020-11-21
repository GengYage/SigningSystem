package com.henu.android.activity.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;


import com.henu.android.activity.R;
import com.henu.android.utils.GroupUtils;

import static com.henu.android.activity.R.*;

public class Main extends Activity {

    public String[] group = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle userInfo = intent.getExtras();

        Thread t = null;
        if(userInfo!=null) {
            t = new Thread() {
                @Override
                public void run() {
                    group = GroupUtils.getGroupsName(Integer.parseInt(userInfo.getString("id")));
                }
            };
            t.start();
        }
        //等待子线程执行完毕
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setContentView(layout.activity_main);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,group);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        Spinner groupSpinner = findViewById(R.id.group);
        groupSpinner.setAdapter(adapter);

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String groupName = (String)adapterView.getItemAtPosition(i);
                Toast.makeText(Main.this,groupName,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        for (int i = 0; i <= 3; i++) {
            PersonChat personChat = new PersonChat();
            personChat.setMeSend(false);
            personChats.add(personChat);
        }
        lv_chat_dialog = findViewById(R.id.lv_chat_dialog);
        Button btn_chat_message_send = findViewById(R.id.btn_chat_message_send);
        final EditText et_chat_message = findViewById(R.id.et_chat_message);

        chatAdapter = new ChatAdapter(this, personChats);
        lv_chat_dialog.setAdapter(chatAdapter);

        btn_chat_message_send.setOnClickListener(new OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (TextUtils.isEmpty(et_chat_message.getText().toString())) {
                    Toast.makeText(Main.this, "发送内容不能为空", 0).show();
                    return;
                }
                PersonChat personChat = new PersonChat();
                //代表自己发送
                personChat.setMeSend(true);
                //得到发送内容
                personChat.setChatMessage(et_chat_message.getText().toString());
                //加入集合
                personChats.add(personChat);
                //清空输入框
                et_chat_message.setText("");
                //刷新ListView
                chatAdapter.notifyDataSetChanged();
                handler.sendEmptyMessage(1);
            }
        });

    }


    private ChatAdapter chatAdapter;
    /**
     * 声明ListView
     */
    private ListView lv_chat_dialog;
    /**
     * 集合
     */
    private List<PersonChat> personChats = new ArrayList<PersonChat>();
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    lv_chat_dialog.setSelection(personChats.size());
                    break;
                default:
                    break;
            }
        }
    };

}

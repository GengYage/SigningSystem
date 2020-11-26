package com.henu.android.activity.home;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.henu.android.activity.R;
import com.henu.android.entity.Group;
import com.henu.android.entity.User;
import com.henu.android.utils.BundleUtils;
import com.henu.android.utils.GroupUtils;
import com.henu.android.utils.MessageUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Home extends Activity implements DGMessage.OnSendMsg, DGMy.OnMyClick {

    private String[] group = null; //存储群组信息
    private User user = null; //用户信息
    private ArrayList<Message> messages = null; //消息源
    private MessageAdapter messageAdapter = null; //消息适配器
    private ListView listView = null; //聊天界面
    private Context context = this;
    private ArrayList<Fragment> frags = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取用户信息
        Bundle userInfo = getIntent().getExtras();
        this.user = BundleUtils.getUserFromBundle(userInfo);

        //初始化聊天数据
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    //查找群聊内容
                    messages = MessageUtils.getMessageByGroupId(1);
                    //查找所有的群
                    group    = GroupUtils.getGroupName(user.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.home);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //设置切换fragment
        ImageView dgMessage = findViewById(R.id.dg_message);
        ImageView dgMy = findViewById(R.id.dg_my);
        dgMessage.setOnClickListener(listener);
        dgMy.setOnClickListener(listener);
        initFragment(new DGMy());
    }

    //切换fragment事件监听器
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DGMessage dgMessage = null;
            DGMy dgMy = null;
            switch (view.getId()) {
                case R.id.dg_message:
                    dgMessage = new DGMessage();
                    addFragment(dgMessage);
                    showFragment(dgMessage);
                    break;
                case R.id.dg_my:
                    dgMy = new DGMy();
                    addFragment(dgMy);
                    showFragment(dgMy);
                    break;
                default:
                    break;
            }
        }
    };

    //设置初识fragment
    public void initFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().add(R.id.tab_fragment,fragment).commit();
        getFragmentManager().beginTransaction().show(fragment).commit();
    }

    //显示指定fragment
    public void showFragment(Fragment fragment) {
        for (Fragment frag :
                frags) {
            if(fragment != null) {
                getFragmentManager().beginTransaction().hide(fragment).commit();
            }
        }
        getFragmentManager().beginTransaction().show(fragment).commit();
    }

    //添加fragment
    public void addFragment(Fragment fragment) {
        if(!fragment.isAdded()) {
            getFragmentManager().beginTransaction().add(R.id.tab_fragment,fragment).commit();
        }
    }

    //聊天fragment
    @Override
    public void sendMsg(String context) {
        Message message = new Message();
        message.setUser(user);
        message.setContext(context);
        message.setGroup(new Group(1,"G","gyg"));
        messages.add(message);
        System.out.println(message.toString());
    }

    @Override
    public MessageAdapter getMessageAdapter() {
        return new MessageAdapter(context,messages,user.getId());
    }

    @Override
    public ArrayList<Message> getMessage() {
        return messages;
    }

    //My fragment
    @Override
    public ArrayAdapter<String> getArrayAdapt() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,group);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        return adapter;
    }

    @Override
    public User setUserInfo() {
        return user;
    }

    @Override
    public void addGroup(String gname) {
        final int[] result = {0};
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    result[0] = GroupUtils.addGroup(gname, user.getUsername());
                    group = GroupUtils.getGroupName(user.getId()); //更新群信息
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.run();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(result[0] == -1) {
            Toast.makeText(this,"创建群聊失败",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"创建群聊成功",Toast.LENGTH_SHORT).show();
            //更新
            DGMy dgMy = new DGMy();
            addFragment(dgMy);
            showFragment(dgMy);
        }
    }

    @Override
    public void addUser(String gname) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    GroupUtils.addUser(gname, user.getId());
                    group = GroupUtils.getGroupName(user.getId()); //更新群信息
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.run();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //更新
        DGMy dgMy = new DGMy();
        addFragment(dgMy);
        showFragment(dgMy);
        Toast.makeText(this,"加入群聊成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public String[] getGroups() {
        return group;
    }
}

package com.henu.android.activity.home;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.henu.android.activity.R;
import com.henu.android.entity.Group;
import com.henu.android.entity.News;
import com.henu.android.entity.User;
import com.henu.android.socket.Client;
import com.henu.android.utils.BundleUtils;
import com.henu.android.utils.GroupUtils;
import com.henu.android.utils.JSONUtils;
import com.henu.android.utils.MessageUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class Home extends Activity implements DGMessage.OnSendMsg, DGMy.OnMyClick {

    private String[] groupUserNoExist = null;
    private ArrayList<Group> groupUserExist = null;
    private User user = null; //用户信息
    private ArrayList<News> myMessages = null; //消息源
    private MessageAdapter messageAdapter = null; //消息适配器
    private ListView listView = null; //聊天界面
    private Context context = this;
    private ArrayList<Fragment> frags = new ArrayList<>();
    public static Looper looper = Looper.myLooper();

    //进入的群id
    private Integer gid;

    //socket
    private Client mClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取用户信息
        Bundle userInfo = getIntent().getExtras();
        user = BundleUtils.getUserFromBundle(userInfo);
        setContentView(R.layout.home);
        //设置切换fragment
        ImageView dgMessage = findViewById(R.id.dg_message);
        ImageView dgMy = findViewById(R.id.dg_my);
        dgMessage.setOnClickListener(listener);
        dgMy.setOnClickListener(listener);

        mClient = new Client(handler);
        mClient.start();
        //初始化信息
        updateInfo();
    }


    public Handler handler = new Handler(looper) {
        @Override
        public void handleMessage(android.os.Message msg) {
            Context cont = null;
            DGMy dgMy = null;
            int what = msg.what;
            switch (what) {
                case 0:
                    News news = JSONUtils.JsonToMessage(msg.obj.toString());
                    if (news.getGroupID() == gid && news.getUserId()!=user.getId()) {
                        myMessages.add(news);
                        Message message = new Message();
                        message.what = 3;
                        DGMessage.handler.sendMessage(message);
                        System.out.println("yes???");
                    } else if(news.getGroupID() != gid && news.getUserId()!=user.getId()){
                        Toast.makeText(context,"群id为"+news.getGroupID()+"的群有新消息",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4:
                    cont = (Context) msg.obj;
                    Toast.makeText(cont, "初始化完成", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    //更新
                    cont = (Context) msg.obj;
                    dgMy = new DGMy();
                    addFragment(dgMy);
                    showFragment(dgMy);
                    Toast.makeText(cont, "加入群聊成功", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    cont = (Context) msg.obj;
                    Toast.makeText(cont, "创建群聊失败,该群已存在", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    cont = (Context) msg.obj;
                    Toast.makeText(cont, "创建群聊成功", Toast.LENGTH_SHORT).show();
                    //更新
                    dgMy = new DGMy();
                    addFragment(dgMy);
                    showFragment(dgMy);
                    break;
                case 9: //进去群聊
                    gid = (Integer) msg.obj;
                    enterGroup(gid);
                    break;
                case 10:
                    DGMessage dgMsg = new DGMessage();
                    addFragment(dgMsg);
                    showFragment(dgMsg);
                    break;
                case 11:  //查询未入群聊信息
                    System.out.println("del group");
                    updateGroupNoExist();
                    break;
                case 12:  //更新未入群聊信息
                    dgMy = new DGMy();
                    addFragment(dgMy);
                    showFragment(dgMy);
                    break;
                default:
                    break;
            }
        }
    };


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
        getFragmentManager().beginTransaction().add(R.id.tab_fragment, fragment).commit();
        getFragmentManager().beginTransaction().show(fragment).commit();
    }

    //显示指定fragment
    public void showFragment(Fragment fragment) {
        for (Fragment frag :
                frags) {
            if (fragment != null) {
                getFragmentManager().beginTransaction().hide(fragment).commit();
            }
        }
        getFragmentManager().beginTransaction().show(fragment).commit();
    }

    //添加fragment
    public void addFragment(Fragment fragment) {
        if (!fragment.isAdded()) {
            getFragmentManager().beginTransaction().add(R.id.tab_fragment, fragment).commit();
        }
    }

    //聊天fragment
    @Override
    public void sendMsg(String context) {
        Message msg = new Message();
        News news = new News();
        news.setContent(context);
        //设置用户id和群id
        news.setGroupID(gid);
        news.setUserId(user.getId());
        news.setUsername(user.getUsername());
        msg.obj = JSONUtils.messageToJson(news);
        msg.what = 1;
        mClient.getClientOutputThread().sendHandler.sendMessage(msg);
        myMessages.add(news);
    }

    @Override
    public MessageAdapter getMessageAdapter() {
        return new MessageAdapter(context, myMessages, user.getId());
    }

    @Override
    public ArrayList<News> getMessage() {
        return myMessages;
    }

    //My fragment
    @Override
    public ArrayAdapter<String> getArrayAdapt() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, groupUserNoExist);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        return adapter;
    }

    @Override
    public GroupAdapter getGroupArrayAdapter() {
        return new GroupAdapter(context, user.getId(), groupUserExist, handler);
    }

    @Override
    public User setUserInfo() {
        return user;
    }

    @Override
    public void addGroup(String gname) {
        String username = user.getUsername();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = GroupUtils.addGroup(gname, username);
                    Message message = new Message();
                    groupUserNoExist = GroupUtils.getGroupName(user.getId()); //更新群信息
                    if (i == 0) {
                        message.what = 7; //成功
                        message.obj = context;
                        handler.sendMessage(message);
                    } else {
                        message.what = 6; //失败
                        message.obj = context;
                        handler.sendMessage(message);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void addUser(String gname) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GroupUtils.addUser(gname, user.getId());
                    groupUserNoExist = GroupUtils.getGroupName(user.getId()); //更新群信息
                    groupUserExist = GroupUtils.getGroups(user.getId()); //更新群信息
                    Message message = new Message();
                    message.what = 5;
                    message.obj = context;
                    handler.sendMessage(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    @Override
    public String[] getGroups() {
        return groupUserNoExist;
    }

    public void updateInfo() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //查找群聊内容
                    myMessages = MessageUtils.getMessageByGroupId(1);
                    //查找用户不在的所有群
                    groupUserNoExist = GroupUtils.getGroupName(user.getId());
                    //查找用户在的所有群
                    groupUserExist = GroupUtils.getGroups(user.getId());

                    Message message = new Message();
                    message.what = 4;
                    message.obj = context;
                    handler.sendMessage(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void enterGroup(int gid) {
        new Thread() {
            @Override
            public void run() {
                try {
                    //查找群聊内容
                    myMessages = MessageUtils.getMessageByGroupId(gid);
                    Message message = new Message();
                    message.what = 10;
                    handler.sendMessage(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void updateGroupNoExist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    groupUserNoExist = GroupUtils.getGroupName(user.getId());
                    Message message = new Message();
                    message.what = 12;
                    handler.sendMessage(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

package com.henu.android.activity.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.henu.android.R;
import com.henu.android.entity.Group;
import com.henu.android.entity.News;
import com.henu.android.entity.User;
import com.henu.android.socket.Client;
import com.henu.android.utils.BundleUtils;
import com.henu.android.utils.GroupUtils;
import com.henu.android.utils.JSONUtils;
import com.henu.android.utils.MessageUtils;
import com.henu.android.utils.SignUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private List<Integer> sids = null;
    //进入的群id
    private Integer gid;
    //socket
    private Client mClient;

    //location
    public static Location locations = null;

    //签到表id
    int[] signId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, //指定gps提供者
                1000, //间隔时间
                1,//位置间隔
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                    }
                }
        );

        locations = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

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

    public void updateLocation(Location location) {
        if(location != null) {
            locations = location;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("您的位置是:\n");
            stringBuilder.append("经度:");
            stringBuilder.append(location.getLongitude());
            stringBuilder.append("\n纬nti度:");
            stringBuilder.append(location.getLatitude());
            Toast.makeText(context,stringBuilder.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,"没有gps信息", Toast.LENGTH_SHORT).show();
        }
    }

    //异步消息处理
    public Handler handler = new Handler(looper) {
        @Override
        public void handleMessage(android.os.Message msg) {
            Context cont = null;
            DGMy dgMy = null;
            int what = msg.what;
            switch (what) {
                case 0:
                    News news = JSONUtils.JsonToMessage(msg.obj.toString());
                    if(news.getGroupID() == gid && news.getUserId()!=user.getId() && MessageAdapter.isInteger(news.getContent())) {
                        System.out.println("ok?");
                        myMessages.add(news);
                        Message message = new Message();
                        message.what = 3;
                        DGMessage.handler.sendMessage(message);
                    } else if (news.getGroupID() == gid && news.getUserId()!=user.getId()) {
                        myMessages.add(news);
                        Message message = new Message();
                        message.what = 3;
                        DGMessage.handler.sendMessage(message);
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
                case 13: //创建群聊
                    getNextSignid();
                case 14:
                    if(msg.obj == null) {
                        return;
                    }
                    createSign((Integer) msg.obj);
                    News newMsg = new News();
                    newMsg.setContent(String.valueOf((Integer) msg.obj));
                    newMsg.setGroupID(gid);
                    newMsg.setUsername(user.getUsername());
                    newMsg.setUserId(user.getId());
                    myMessages.add(newMsg);

                    Message message = new Message();
                    message.what = 3;
                    DGMessage.handler.sendMessage(message);

                    Message message1 = new Message();
                    message1.what = 1;
                    message1.obj = JSONUtils.messageToJson(newMsg);
                    mClient.getClientOutputThread().sendHandler.sendMessage(message1);
                    break;
                case 15:
                    Toast.makeText(context,"创建成功",Toast.LENGTH_SHORT).show();
                    break;
                case 16:
                    if((Integer)msg.obj == 1) {
                        Toast.makeText(context,"签到成功",Toast.LENGTH_SHORT).show();
                    } else if((Integer)msg.obj == 0){
                        Toast.makeText(context,"签到已经结束",Toast.LENGTH_SHORT).show();
                    } else if((Integer)msg.obj == 2) {
                        Toast.makeText(context,"未在签到范围内",Toast.LENGTH_SHORT).show();
                    }
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
    public void sendMsg(String content) {
        News news = new News();
        Message msg = new Message();
        //创建群指令
        if("/create sign".equals(content)) {
            Toast.makeText(context,"正在创建签到，请稍候",Toast.LENGTH_SHORT).show();
            Message message = new Message();
            message.what = 13;
            news.setContent(content);
            //设置用户id和群id
            news.setGroupID(gid);
            news.setUserId(user.getId());
            news.setUsername(user.getUsername());
            handler.sendMessage(message);

            msg.obj = JSONUtils.messageToJson(news);
            msg.what = 1;
            myMessages.add(news);
        } else if("".equals(content)) {
            Toast.makeText(context,"消息不能为空",Toast.LENGTH_SHORT).show();
        } else {
            news.setContent(content);
            //设置用户id和群id
            news.setGroupID(gid);
            news.setUserId(user.getId());
            news.setUsername(user.getUsername());

            msg.obj = JSONUtils.messageToJson(news);
            msg.what = 1;
            myMessages.add(news);
        }
        mClient.getClientOutputThread().sendHandler.sendMessage(msg);
    }

    //判断一个数是否在数组
    public boolean isNumber(int num, int[] arr) {
        //没有签到表时
        if(arr == null) {
            return false;
        }
        boolean flag = false;
        for (int i=0;i<arr.length;i++) {
            if (num == arr[i]) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    //查找下一个可用signIn id
    public void getNextSignid() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int nextSignId = SignUtils.getNextSignId();
                    Message message = new Message();
                    message.what = 14;
                    message.obj = nextSignId;
                    handler.sendMessage(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //创建签到表
    public void createSign(int sid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SignUtils.createSign(sid, gid, user.getId(), Double.parseDouble(String.valueOf(locations.getLongitude())), Double.parseDouble(String.valueOf(locations.getLatitude())));
                    Message message = new Message();
                    message.what = 15;
                    handler.sendMessage(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public MessageAdapter getMessageAdapter() {
        return new MessageAdapter(context, myMessages, user.getId(),handler);
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

    //获取用户加入的签到表的sid
    @Override
    public SignInAdapter getSids() {
        return new SignInAdapter(context, user.getId(), sids,handler);
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
                    //查找用户参与的所有签到
                    sids = SignUtils.getAllSidByUid(user.getId());

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

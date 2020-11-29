package com.henu.android.activity.home;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.henu.android.activity.R;
import com.henu.android.entity.Group;
import com.henu.android.utils.GroupUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class GroupAdapter extends BaseAdapter {
    private ArrayList<Group> groups; //数据源
    private Context context; //上下文
    private LayoutInflater inflater; //反射器
    private int id; //记录用户的id
    private Handler handler;

    public GroupAdapter(Context context, int id,ArrayList<Group> groups, Handler handler) {
        super();
        this.context = context;
        this.id = id;
        this.groups = groups;
        this.handler = handler;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int i) {
        return groups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return groups.get(i).getGid();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View group = inflater.inflate(R.layout.group, null);
        TextView groupName = group.findViewById(R.id.group_name);
        groupName.setText(groups.get(i).getGname());
        Button delButton = group.findViewById(R.id.group_del);
        Button enterButton = group.findViewById(R.id.group_enter);
        delButton.setTag(groups.get(i).getGid());
        enterButton.setTag(groups.get(i).getGid());

        delButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer index = (Integer) delButton.getTag();
                delGroup((Integer) delButton.getTag(),id);
                groups.remove(i);
                notifyDataSetChanged();
            }
        });
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGroupId((Integer) enterButton.getTag());
            }
        });

        return group;
    }

    public void delGroup(int groupid, int memberid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GroupUtils.delGroupMember(groupid,memberid);
                    Message message = new Message();
                    message.what = 11;
                    handler.sendMessage(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void setGroupId(int gid) {
        Message message = new Message();
        message.what = 9;
        message.obj = gid;
        handler.sendMessage(message);
    }

}

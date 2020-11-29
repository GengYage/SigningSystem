package com.henu.android.activity.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.henu.android.activity.R;
import com.henu.android.entity.News;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {
    private ArrayList<News> news; //数据源
    private Context context; //上下文
    private LayoutInflater inflater; //反射器
    private int id; //记录用户的id
    //构造器
    public MessageAdapter (Context context, ArrayList<News> news, int id){
        super();
        this.context = context;
        this.news = news;
        this.id = id;
        this.inflater = LayoutInflater.from(context);
    }
    //返回是否是自己发送的消息
    public boolean isMyMessage(News news) {
        return news.getUserId() == this.id;
    }

    //获取数据源的个数
    @Override
    public int getCount() {
        return news.size();
    }

    //返回传入索引的对象
    @Override
    public Object getItem(int i) {
        return news.get(i);
    }

    //返回对象的id
    @Override
    public long getItemId(int i) {
        return i;
    }

    //获取视图
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View msg = null;
        TextView msg_context = null;
        TextView msg_name = null;
        if(isMyMessage(news.get(i))) {
            msg = inflater.inflate(R.layout.right_msg,null);
            msg_context = msg.findViewById(R.id.msg_right_context);
            msg_context.setText(news.get(i).getContent());
        } else {
            msg = inflater.inflate(R.layout.left_msg,null);
            msg_context = msg.findViewById(R.id.msg_left_context);
            msg_context.setText(news.get(i).getContent());
            msg_name = msg.findViewById(R.id.msg_name);
            msg_name.setText(news.get(i).getUsername());
        }
        return msg;
    }
}

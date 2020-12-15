package com.henu.android.activity.home;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.henu.android.R;
import com.henu.android.entity.Group;
import com.henu.android.entity.SignIn;

import java.util.ArrayList;
import java.util.List;

public class SignInAdapter extends BaseAdapter {
    private List<Integer> signIns; //数据源
    private Context context; //上下文
    private LayoutInflater inflater; //反射器
    private int id; //记录用户的id
    private Handler handler;

    public SignInAdapter(Context context, int id,List<Integer> signIns, Handler handler) {
        super();
        this.context = context;
        this.id = id;
        this.signIns = signIns;
        this.handler = handler;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return signIns.size();
    }

    @Override
    public Object getItem(int i) {
        return signIns.get(i);
    }

    @Override
    public long getItemId(int i) {
        return signIns.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View signIn = inflater.inflate(R.layout.signin, null);
        Button viewById = signIn.findViewById(R.id.sigin_btn);
        viewById.setText(signIns.get(i).toString());
        return signIn;
    }
}

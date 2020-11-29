package com.henu.android.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.henu.android.activity.R;
import com.henu.android.entity.Group;
import com.henu.android.entity.User;

public class DGMy extends Fragment {
    public interface OnMyClick {
        ArrayAdapter<String> getArrayAdapt(); //获取未加入群adapter
        GroupAdapter getGroupArrayAdapter(); //获取加入群adapter
        User setUserInfo(); //获取用户信息
        void addGroup(String gname); //添加群
        void addUser(String gname); //加群
        String[] getGroups();
    }
    private OnMyClick onMyClick;
    private Button createGroup = null; //创群按钮
    private Button joinGroup = null; //加群按钮
    private EditText groupName = null; //群名编辑框
    private Spinner groups = null; //所有群
    private String gName = null; //群名
    private ArrayAdapter<String> arrayAdapt = null; //未加入群适配器
    private GroupAdapter groupArrayAdapter = null; //已加入群适配器
    private ListView myGroup = null; //已加入群的view

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onMyClick = (DGMy.OnMyClick)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"must implements OnMyClick");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dgmy,container,false);
        //创建群操作
        groupName = view.findViewById(R.id.group_create_edit);
        createGroup = view.findViewById(R.id.group_create_btn);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMyClick.addGroup(groupName.getText().toString());
                //清空输入
                groupName.setText("");
                arrayAdapt = onMyClick.getArrayAdapt();
                arrayAdapt.notifyDataSetChanged();
                System.out.println("更新");
            }
        });

        //加群操作
        joinGroup = view.findViewById(R.id.confirm_join);
        groups = view.findViewById(R.id.join_group);
        arrayAdapt = onMyClick.getArrayAdapt();
        groups.setAdapter(onMyClick.getArrayAdapt());
        groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gName = (String)adapterView.getItemAtPosition(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //确认即加入
                onMyClick.addUser(gName);
            }
        });

        //已加入群聊信息
        groupArrayAdapter = onMyClick.getGroupArrayAdapter();
        myGroup = view.findViewById(R.id.my_group);
        myGroup.setAdapter(groupArrayAdapter);
        groupArrayAdapter.notifyDataSetChanged();


        return view;
    }

}

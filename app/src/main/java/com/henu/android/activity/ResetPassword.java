package com.henu.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.henu.android.R;
import com.henu.android.entity.User;
import com.henu.android.utils.MysqlUtils;

import java.sql.SQLException;

public class ResetPassword  extends Activity implements View.OnClickListener {

    private Button btnSure;
    private Button btnCancel;
    private EditText edAccount;
    private EditText edOldPwd;
    private EditText edNewPwd;
    private EditText edNewPwdCheck;
    private String telNumber;
    private String oldPwd;
    private String newPwd;
    private String newPwdCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        init();
    }

    public void init(){
        btnSure = findViewById(R.id.resetpwd_btn_sure);
        btnCancel = findViewById(R.id.resetpwd_btn_cancel);
        edAccount = findViewById(R.id.resetpwd_edit_account);
        edOldPwd = findViewById(R.id.resetpwd_edit_pwd_old);
        edNewPwd = findViewById(R.id.resetpwd_edit_pwd_new);
        edNewPwdCheck = findViewById(R.id.resetpwd_edit_pwd_check);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.resetpwd_btn_sure:
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            resetPwd();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.resetpwd_btn_cancel:
                Intent intent = new Intent(this,Login.class);
                startActivity(intent);
                break;
        }

    }

    public void resetPwd() throws SQLException, ClassNotFoundException {
        telNumber = edAccount.getText().toString();
        oldPwd = edOldPwd.getText().toString();
        newPwd = edNewPwd.getText().toString();
        newPwdCheck = edNewPwdCheck.getText().toString();
        User user = null;
        if(MysqlUtils.isAccountExist(telNumber)) {
            user = MysqlUtils.findUserByTel(telNumber);

            if(oldPwd.equals(user.getPassword())) {
                if (!oldPwd.equals(newPwd)) {
                    if (newPwd.equals(newPwdCheck)) {
                        MysqlUtils.updateAccount(telNumber, newPwd);
                        Intent intent = new Intent(this, Login.class);
                        startActivity(intent);
                    } else {
                        System.out.println("=========两次密码输入不一致=======");
                    }
                } else {
                    System.out.println("==========新密码和旧密码相同========");
                }
            }else{
                System.out.println("==========密码输入错误==========");
            }
        }else{
            System.out.println("===========该帐号不存在==========");
        }
    }
}

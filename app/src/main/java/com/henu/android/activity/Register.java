package com.henu.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.henu.android.R;
import com.henu.android.entity.User;
import com.henu.android.utils.MysqlUtils;

import java.sql.SQLException;

import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;


public class Register extends Activity implements View.OnClickListener , OnSendMessageHandler {

    private Button btnSure;
    private Button btnCancel;
    private Button btnGainVerify;
    private EditText edName;
    private EditText edAccount;
    private EditText edPassword;
    private EditText edPasswordSure;
    private EditText edVerify;
    private String telName;
    private String password;
    private String passwordSure;
    private String username;

    EventHandler eh=new EventHandler(){

        @Override
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                register();
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    //获取验证码成功
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                }
            }else{
                ((Throwable)data).printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        init();
    }

    public  void init(){
        btnSure = findViewById(R.id.register_btn_sure);
        btnCancel = findViewById(R.id.register_btn_cancel);
        btnGainVerify = findViewById(R.id.register_btn_gain_verify);
        edName = findViewById(R.id.register_edit_name);
        edAccount = findViewById(R.id.register_edit_account);
        edPassword = findViewById(R.id.register_edit_pwd);
        edPasswordSure = findViewById(R.id.register_edit_pwd_again);
        edVerify = findViewById(R.id.register_edit_verify_code);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnGainVerify.setOnClickListener(this);
        SMSSDK.registerEventHandler(eh); //注册短信回调

    }


    @Override
    public void onClick(View v) {
        String phone = edAccount.getText().toString();
        Intent intent;
        switch (v.getId()){
            case R.id.register_btn_sure:
                String code = edVerify.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    return;
                }
                SMSSDK.submitVerificationCode("86", phone, code); // 校验短信验证
                intent = new Intent(Register.this, Login.class);
                startActivity(intent);

                break;
            case R.id.register_btn_gain_verify:
                if (TextUtils.isEmpty(phone)) {
                    return;
                }
                //中国大陆区域 +86
                SMSSDK.getVerificationCode("86", phone,  this);// 获取短信验证码
                break;
            case R.id.register_btn_cancel:
                intent = new Intent(Register.this,Login.class);
                startActivity(intent);
        }
    }

    public void register() throws SQLException, ClassNotFoundException {
        username = edName.getText().toString();
        telName = edAccount.getText().toString();
        password = edPassword.getText().toString();
        passwordSure = edPasswordSure.getText().toString();
        User user = new User(telName,username,password);

        if(password.equals(passwordSure)){
            if(!MysqlUtils.isAccountExist(telName)){
                MysqlUtils.addAccount(user);
                Intent intent = new Intent(this,Login.class);
                startActivity(intent);
            }
        }else{
            Toast.makeText(this,"两次密码输入的不一致",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onSendMessage(String s, String s1) {
        return false;
    }
}

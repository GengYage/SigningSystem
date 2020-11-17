package com.henu.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.henu.android.entity.User;
import com.henu.android.utils.MysqlUtils;

import java.sql.SQLException;


public class Login extends Activity implements View.OnClickListener {
    private Button  btnLogin;
    private Button  btnRegister;
    private TextView tvResetPwd;
    private EditText    edAccount;
    private EditText    edPassword;
    private String telNumber;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
    }

    public void init(){
        btnLogin = findViewById(R.id.login_btn_login);
        btnRegister = findViewById(R.id.login_btn_register);
        edAccount = findViewById(R.id.login_edit_account);
        edPassword = findViewById(R.id.login_edit_pwd);
        tvResetPwd = findViewById(R.id.login_text_change_pwd);


        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        tvResetPwd.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.login_btn_login:
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            login();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.login_text_change_pwd:
                intent = new Intent(this,ResetPassword.class);
                startActivity(intent);
                break;
            case R.id.login_btn_register:
                intent = new Intent(Login.this,Register.class);
                startActivity(intent);
        }
    }

    public void login() throws SQLException, ClassNotFoundException {
        telNumber = edAccount.getText().toString();
        password = edPassword.getText().toString();
        User user = null;
        user = MysqlUtils.findUserByTel(telNumber);
        System.out.println(telNumber+" "+user);
        if(user != null){
            if(password.equals(user.getPassword())){
                Intent intent = new Intent(Login.this,Home.class);
                startActivity(intent);
            }
            else{
                System.out.println("=======密码错误======");
            }
        }
        else{
            System.out.println("=======帐号不存在=======");
        }
    }
}

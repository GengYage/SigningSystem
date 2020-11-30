package com.henu.android.utils;

import com.henu.android.entity.SignIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SignUtils {

    //获取所有签到表
    public static ArrayList<SignIn> getSigns() throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "select * from signin";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ResultSet resultSet = ptmt.executeQuery();
        ArrayList<SignIn> signIns = new ArrayList<>();
        while (resultSet.next()) {
            SignIn signIn = new SignIn();
            signIn.setId(resultSet.getInt(1));
            signIn.setGid(resultSet.getInt(2));
            signIn.setUid(resultSet.getInt(3));
            signIn.setTime(resultSet.getTime(4));
            signIn.setDeadLine(resultSet.getTime(5));
            signIn.setLongitude(resultSet.getDouble(6));
            signIn.setLatitude(resultSet.getDouble(7));
            signIn.setRange(resultSet.getDouble(8));
            signIn.setoLongitude(resultSet.getDouble(9));
            signIn.setoLatitude(resultSet.getDouble(10));
            signIn.setState(resultSet.getInt(11));
            signIns.add(signIn);
        }
        return signIns;
    }

    //获取所有签到表的id
    public static int[] getAllSignId() {
        ArrayList<SignIn> signs = null;
        try {
            signs = SignUtils.getSigns();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        int[] names = new int[signs.size()];

        int index = 0;
        for (SignIn sign :
                signs) {
            names[index] = sign.getId();
            index++;
        }

        return names;
    }

    //创建签到表
    public static void createSign(int gid,int uid,double longitude,double latitude) throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "insert into signin values(null,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ptmt.setInt(1,gid);
        ptmt.setInt(2,uid);
        Date date = new Date();
        ptmt.setDate(3,new java.sql.Date(date.getTime()));
        ptmt.setDate(4,new java.sql.Date(date.getTime()+60*15*1000));
        ptmt.setDouble(5,longitude);
        ptmt.setDouble(6,latitude);
        ptmt.setDouble(7,0.5);
        ptmt.setDouble(8,longitude);
        ptmt.setDouble(9,latitude);
        ptmt.setInt(10,1);
        ptmt.execute();
        connection.close();
    }

    public static int getNextSignId() throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "SELECT auto_increment FROM information_schema.`TABLES` WHERE TABLE_SCHEMA='android' AND TABLE_NAME='signin'";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ResultSet resultSet = ptmt.executeQuery();
        int index = 0;
        while (resultSet.next()) {
            index = resultSet.getInt(1);
        }
        return index;
    }

}

package com.henu.android.utils;

import com.henu.android.entity.SignIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        int[] names = new int[signs.size()];

        int index = 0;
        for (SignIn sign :
                signs) {
            names[index] = sign.getSid();
            index++;
        }

        return names;
    }

    //创建签到表
    public static void createSign(int sid,int gid,int uid,double longitude,double latitude) throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "insert into signin values(null,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ptmt.setInt(1,sid);
        ptmt.setInt(2,gid);
        ptmt.setInt(3,uid);
        Date date = new Date();
        ptmt.setDate(4,new java.sql.Date(date.getTime()));
        ptmt.setDate(5,new java.sql.Date(date.getTime()+60*15*1000));
        ptmt.setDouble(6,longitude);
        ptmt.setDouble(7,latitude);
        ptmt.setDouble(8,0.5);
        ptmt.setDouble(9,longitude);
        ptmt.setDouble(10,latitude);
        ptmt.setInt(11,1);
        ptmt.execute();
        connection.close();
    }

    //获取下一个签到表id
    public static int getNextSignId() throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "SELECT auto_increment FROM information_schema.`TABLES` WHERE TABLE_SCHEMA='android' AND TABLE_NAME='signin'";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ResultSet resultSet = ptmt.executeQuery();
        int index = 0;
        while (resultSet.next()) {
            index = resultSet.getInt(1);
        }
        connection.close();
        return index;
    }

    //通过群id获取签到信息
    public static SignIn getSignBySid(int id) throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "select * from signin where sid = ?";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ptmt.setInt(1,id);
        ResultSet resultSet = ptmt.executeQuery();
        SignIn signIn = null;
        while (resultSet.next()) {
            signIn = new SignIn();
            signIn.setId(resultSet.getInt(1));
            signIn.setSid(resultSet.getInt(2));
            signIn.setGid(resultSet.getInt(3));
            signIn.setUid(resultSet.getInt(4));
            signIn.setTime(resultSet.getTime(5));
            signIn.setDeadLine(resultSet.getTime(6));
            signIn.setLongitude(resultSet.getDouble(7));
            signIn.setLatitude(resultSet.getDouble(8));
            signIn.setRange(resultSet.getDouble(9));
            signIn.setoLongitude(resultSet.getDouble(10));
            signIn.setoLatitude(resultSet.getDouble(11));
            signIn.setState(resultSet.getInt(12));
        }
        connection.close();
        return signIn;
    }

    //通过singin sid签到
    public static int signOneById(int id, int uid, double longitude, double latitude) throws SQLException, ClassNotFoundException {
        //查询签到信息
        SignIn signByid = SignUtils.getSignBySid(id);
        System.out.println(signByid);
        if(signByid.getUid() == uid) {
            return 1; //发起者默认签到成功
        }
        int flag = -1;

        SignIn signIn = new SignIn();
        signIn.setId(id);
        signIn.setSid(signIn.getSid());
        signIn.setUid(uid);
        signIn.setGid(signByid.getUid());
        signIn.setLatitude(signByid.getLatitude());
        signIn.setLongitude(signByid.getLongitude());
        signIn.setRange(signByid.getRange());
        signIn.setTime(signByid.getTime());

        if(signByid.getDeadLine().getTime() < new Date().getTime()) {
            flag = 0; //过时
            signIn.setDeadLine(new Date());
            signByid.setState(flag);
        }

        if(signByid.getLongitude()+signByid.getRange() > longitude
                && signByid.getLatitude()+signByid.getRange() > latitude
                && signByid.getLongitude()-signByid.getRange() < longitude
                && signByid.getLatitude()-signByid.getRange() < latitude) {
            //位置信息正确
            signByid.setoLatitude(latitude);
            signByid.setoLongitude(longitude);
            signByid.setState(1);
            flag = 1; //签到成功
            signByid.setState(flag);
        } else {
            flag = 2; //位置不正确
            signByid.setState(flag);
        }
        insertSignIn(signIn);
        return flag;
    }

    public static void insertSignIn(SignIn signIn) throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "insert into signin values(null,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ptmt.setInt(1,signIn.getSid());
        ptmt.setInt(2,signIn.getGid());
        ptmt.setInt(3,signIn.getUid());
        ptmt.setDate(4,new java.sql.Date(signIn.getTime().getTime()));
        ptmt.setDate(5,new java.sql.Date(signIn.getDeadLine().getTime()));
        ptmt.setDouble(6,signIn.getLongitude());
        ptmt.setDouble(7,signIn.getLatitude());
        ptmt.setDouble(8,0.5);
        ptmt.setDouble(9,signIn.getoLongitude());
        ptmt.setDouble(10,signIn.getoLatitude());
        ptmt.setInt(11,signIn.getState());
        ptmt.execute();
        connection.close();
    }

    //查找所有签到sid
    public static List<Integer> getAllSidByUid(int uid) throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "SELECT DISTINCT sid FROM signin WHERE uid = ?";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ptmt.setInt(1,uid);
        ResultSet resultSet = ptmt.executeQuery();
        List<Integer> sids = new ArrayList<>();
        while (resultSet.next()) {
            sids.add(resultSet.getInt(1));
        }
        return sids;
    }
}

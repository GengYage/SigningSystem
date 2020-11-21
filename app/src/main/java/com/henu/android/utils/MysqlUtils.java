package com.henu.android.utils;

import com.henu.android.entity.Group;
import com.henu.android.entity.GroupMember;
import com.henu.android.entity.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MysqlUtils {
    private static String url = "jdbc:mysql://120.55.194.26/android?useUnicode=true&characterEncoding=utf-8&useSSL=true";
    private static String username = "android";
    private static String password = "gyg06103234";

    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url,username,password);
        return connection;
    }

    public static User findUserById(int id) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        String sql = "select * from user where id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,Integer.toString(id));
        ResultSet resultSet = preparedStatement.executeQuery();


        //如果帐号存在，则返回，否则返回一个空指针
        User user = null;
        while(resultSet.next()){
            user = new User();
            user.setId((int) resultSet.getObject("id"));
            user.setTelNumber((String) resultSet.getObject("telNumber"));
            user.setUsername((String) resultSet.getObject("username"));
            user.setPassword((String) resultSet.getObject("password"));
        }

        resultSet.close();
        connection.close();
        return user;
    }

    public static User findUserByTel(String telNumber) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        String sql = "select * from user where telNumber = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,telNumber);
        ResultSet resultSet = preparedStatement.executeQuery();


        //如果帐号存在，则返回，否则返回一个空指针
        User user = null;
        while(resultSet.next()){    user = new User();
            user.setId((int) resultSet.getObject("id"));
            user.setTelNumber((String) resultSet.getObject("telNumber"));
            user.setUsername((String) resultSet.getObject("username"));
            user.setPassword((String) resultSet.getObject("password"));
        }

        resultSet.close();
        connection.close();
        return user;
    }


    public static boolean isAccountExist(String telNumber) throws SQLException, ClassNotFoundException {
        if(findUserByTel(telNumber) == null){
            return false;
        }else{
            return true;
        }
    }

    public static void addAccount(User user) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();

        if(isAccountExist(user.getTelNumber())) {
            System.out.println("=======用户已存在=======");
            return;
        }

        //自动递增id
        String sql  = "insert into user (telNumber,username,password) values(?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,user.getTelNumber());
        preparedStatement.setString(2,user.getUsername());
        preparedStatement.setString(3,user.getPassword());
        preparedStatement.executeUpdate();
        connection.close();
        System.out.println("添加成功");
    }

    //更新用户信息
    public static void updateAccount(String telNumber,String password) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        String sql = "update user set password=? where telNumber = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,password);
        preparedStatement.setString(2,telNumber);

        preparedStatement.executeUpdate();
        connection.close();
    }

    //列出存在的所有群
    public static ArrayList<Group> showGroups() throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        String sql = "select * form group";
        PreparedStatement ptmt = conn.prepareStatement(sql);
        ResultSet resultSet = ptmt.executeQuery();

        ArrayList<Group> groups = new ArrayList<>();
        while (resultSet.next()) {
            Group group = new Group();
            group.setGid(resultSet.getInt(1));
            group.setGname(resultSet.getString(2));
            group.setGowner(resultSet.getString(3));
            groups.add(group);
        }
        return groups;
    }

    //通过gid查找群
    public static Group findGroupByGid(int gid) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        String sql = "select * from `group` where gid = ?";
        PreparedStatement ptmt = conn.prepareStatement(sql);
        ptmt.setInt(1,gid);

        ResultSet resultSet = ptmt.executeQuery();
        Group group = null;
        while (resultSet.next()) {
            group = new Group();
            group.setGid(resultSet.getInt(1));
            group.setGname(resultSet.getString(2));
            group.setGowner(resultSet.getString(3));
        }

        return group;
    }

    //通过用户id查找用户所在的群组
    public static ArrayList<Group> findGroupsByUserId(int id) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        String sql = "select * from groupmember where memberid = ?";
        PreparedStatement ptmt = conn.prepareStatement(sql);
        ptmt.setInt(1,id);

        ResultSet resultSet = ptmt.executeQuery();
        ArrayList<GroupMember> groupMembers = new ArrayList<>();
        ArrayList<Group> groups = new ArrayList<>();
        while (resultSet.next()) {
            GroupMember groupMember = new GroupMember();
            groupMember.setGid(resultSet.getInt(1));
            groupMember.setMemberId(resultSet.getInt(2));
            groupMembers.add(groupMember);
        }

        for (GroupMember groupMember :
                groupMembers) {
            Group groupByGid = findGroupByGid(groupMember.getGid());
            groups.add(groupByGid);
        }

        return groups;
    }

}

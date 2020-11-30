package com.henu.android.utils;

import com.henu.android.entity.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GroupUtils {

    //查找所有群
    public static ArrayList<Group> findAllGroup() throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "select * from sgroup";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ResultSet resultSet = ptmt.executeQuery();
        ArrayList<Group> groups = new ArrayList<>();
        while (resultSet.next()) {
            Group group = new Group();
            group.setGid(resultSet.getInt(1));
            group.setGname(resultSet.getString(2));
            group.setGowner(resultSet.getString(3));
            groups.add(group);
        }
        resultSet.close();
        connection.close();
        return groups;
    }
    //返回所有未加入的群
    public static String[] getGroupName(int id) throws SQLException, ClassNotFoundException {
        ArrayList<Group> allGroup = findAllGroup(); //所有的群聊
        ArrayList<String> allGroupName = new ArrayList<>(); //所有群聊的名字
        ArrayList<String> readyGroupName = new ArrayList<>();  //待加入的群聊名字
        int index = 0;
        for (Group group : allGroup) {
            allGroupName.add(group.getGname());
        }
        //用户加入的群
        ArrayList<Group> groupsByUserId = MysqlUtils.findGroupsByUserId(id);

        for (Group group : groupsByUserId) {
            if(group.getGname() != null) {
                readyGroupName.add(group.getGname());
            }
        }

        if(allGroupName.size()-readyGroupName.size() == 0) {
            return new String[0];
        }

        String[] str = new String[allGroupName.size()-readyGroupName.size()];
        for (String gName :
                allGroupName) {
            if(!readyGroupName.contains(gName)){
                str[index] = gName;
                index++;
            }
        }

        return str;
    }

    //通过用户id列出用户所在所有群组
    public static String[] getGroupsName(int id) {
        ArrayList<Group> groups = null;
        try {
            groups = MysqlUtils.findGroupsByUserId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String[] groupNames = new String[groups.size()];

        int index = 0;
        for (Group group :
                groups) {
            groupNames[index] = group.getGname();
            index++;
        }
        return groupNames;
    }

    public static ArrayList<Group> getGroups(int id) {
        ArrayList<Group> groups = null;
        try {
            groups = MysqlUtils.findGroupsByUserId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return groups;
    }

    //通过群id获取群信息
    public static Group getGroupInfoByGid(int gid) throws SQLException, ClassNotFoundException {
        Group group = new Group();
        Connection connection = MysqlUtils.getConnection();
        String sql = "select * from sgroup where gid = ?";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ptmt.setInt(1,gid);
        ResultSet resultSet = ptmt.executeQuery();
        while (resultSet.next()) {
            group.setGid(resultSet.getInt(1));
            group.setGname(resultSet.getString(2));
            group.setGowner(resultSet.getString(3));
        }
        resultSet.close();
        connection.close();
        return group;
    }

    //创建群聊
    public static int addGroup(String gname,String username) throws SQLException, ClassNotFoundException {
        if(findGroupByGname(gname) > 0) {
            return -1;
        }
        Connection connection = MysqlUtils.getConnection();
        String sql = "INSERT INTO sgroup VALUES(null,?,?)";
        PreparedStatement ptmp = connection.prepareStatement(sql);
        ptmp.setString(1,gname);
        ptmp.setString(2,username);
        int tmp = 0;
        if(ptmp.execute()) { //失败
            tmp = -1;
        }
        connection.close();
        return tmp;
    }
    //查找群id
    public static int findGroupByGname(String name) throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "select * from sgroup where gname = ?";
        PreparedStatement ptmp = connection.prepareStatement(sql);
        ptmp.setString(1,name);
        ResultSet resultSet = ptmp.executeQuery();
        int tmp = 0;
        while (resultSet.next()) {
            tmp = resultSet.getInt(1);
        }

        resultSet.close();
        connection.close();
        return tmp;
    }

    //通过群名加群
    public static void addUser(String name, int uid) throws SQLException, ClassNotFoundException {
        int gid = findGroupByGname(name);
        Connection connection = MysqlUtils.getConnection();
        String sql = "INSERT INTO groupmember VALUES(?,?)";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ptmt.setInt(1,gid);
        ptmt.setInt(2,uid);
        boolean execute = ptmt.execute();
        connection.close();
    }

    //退出群聊
    public static void delGroupMember(int gid,int uid) throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "delete from groupmember where groupid  = ? and memberid = ?";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ptmt.setInt(1,gid);
        ptmt.setInt(2,uid);
        ptmt.execute();
        connection.close();
    }

}

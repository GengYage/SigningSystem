package com.henu.android.utils;

import com.henu.android.activity.home.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MessageUtils {
    public static ArrayList<Message>  getMessageByGroupId(int gid) throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "select * from message where gid = ?";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ptmt.setInt(1,gid);
        ResultSet resultSet = ptmt.executeQuery();
        ArrayList<Message> messages = new ArrayList<>();
        while (resultSet.next()){
            Message message = new Message();
            message.setGroup(GroupUtils.getGroupInfoByGid(gid));
            message.setUser(MysqlUtils.findUserById(resultSet.getInt(2)));
            message.setContext(resultSet.getString(3));
            message.setDate(resultSet.getDate(4));
            messages.add(message);
        }
        return messages;
    }
}

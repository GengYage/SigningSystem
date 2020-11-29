package com.henu.android.utils;

import com.henu.android.activity.home.MyMessage;
import com.henu.android.entity.News;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MessageUtils {
    public static ArrayList<News>  getMessageByGroupId(int gid) throws SQLException, ClassNotFoundException {
        Connection connection = MysqlUtils.getConnection();
        String sql = "select * from message where gid = ?";
        PreparedStatement ptmt = connection.prepareStatement(sql);
        ptmt.setInt(1,gid);
        ResultSet resultSet = ptmt.executeQuery();
        ArrayList<News> myMessages = new ArrayList<>();
        while (resultSet.next()){
            News myMessage = new News();
            myMessage.setGroupID(gid);
            myMessage.setUserId(resultSet.getInt(2));
            myMessage.setContent(resultSet.getString(3));
            myMessage.setUsername(resultSet.getString(5));
            myMessages.add(myMessage);
        }
        resultSet.close();
        connection.close();
        return myMessages;
    }
}

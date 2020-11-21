package com.henu.android.utils;

import com.henu.android.entity.Group;

import java.sql.SQLException;
import java.util.ArrayList;

public class GroupUtils {

    //通过id列出用户所在所有群组
    public static String[] getGroupsName(int id) {
        ArrayList<Group> groups = null;
        try {
            groups = MysqlUtils.findGroupsByUserId(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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

}

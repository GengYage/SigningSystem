package com.henu.android;

import com.henu.android.entity.Group;
import com.henu.android.entity.User;
import com.henu.android.utils.GroupUtils;
import com.henu.android.utils.MysqlUtils;

import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws SQLException, ClassNotFoundException {
        try {
            String[] groupName = GroupUtils.getGroupName(1);
            System.out.println(Arrays.toString(groupName));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
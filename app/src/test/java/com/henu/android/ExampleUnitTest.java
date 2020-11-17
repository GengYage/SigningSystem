package com.henu.android;

import com.henu.android.entity.User;
import com.henu.android.utils.MysqlUtils;

import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws SQLException, ClassNotFoundException {


        User user = MysqlUtils.findUserByAccount("1");
        System.out.println(user);
    }
}
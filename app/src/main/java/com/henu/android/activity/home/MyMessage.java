package com.henu.android.activity.home;

import com.henu.android.entity.Group;
import com.henu.android.entity.User;

import java.util.Date;

public class MyMessage {
    private User user;
    private Group group;
    private String context;
    private Date date;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "user=" + user +
                ", group=" + group +
                ", context='" + context + '\'' +
                ", date=" + date +
                '}';
    }
}

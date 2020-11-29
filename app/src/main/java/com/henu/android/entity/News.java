package com.henu.android.entity;

public class News {
    private  int userId;
    private  int groupID;
    private  String content;
    private  String username;

    public News() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "News{" +
                "userId=" + userId +
                ", groupID=" + groupID +
                ", content='" + content + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}


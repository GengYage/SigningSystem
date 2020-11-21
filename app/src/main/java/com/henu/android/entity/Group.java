package com.henu.android.entity;

public class Group {
    private int gid;
    private String gname;
    private String gowner;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getGowner() {
        return gowner;
    }

    public void setGowner(String gowner) {
        this.gowner = gowner;
    }

    @Override
    public String toString() {
        return "Group{" +
                "gid=" + gid +
                ", gname='" + gname + '\'' +
                ", gowner='" + gowner + '\'' +
                '}';
    }
}

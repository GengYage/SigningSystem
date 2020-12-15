package com.henu.android.entity;

import java.util.Date;

public class SignIn {
    private int id;
    private int sid;
    private int gid;
    private int uid;
    private Date time;
    private Date deadLine;
    private double longitude;
    private double latitude;
    private double oLongitude;
    private double oLatitude;
    private double range;
    private int state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getoLongitude() {
        return oLongitude;
    }

    public void setoLongitude(double oLongitude) {
        this.oLongitude = oLongitude;
    }

    public double getoLatitude() {
        return oLatitude;
    }

    public void setoLatitude(double oLatitude) {
        this.oLatitude = oLatitude;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "SignIn{" +
                "id=" + id +
                ", sid=" + sid +
                ", gid=" + gid +
                ", uid=" + uid +
                ", time=" + time +
                ", deadLine=" + deadLine +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", oLongitude=" + oLongitude +
                ", oLatitude=" + oLatitude +
                ", range=" + range +
                ", state=" + state +
                '}';
    }
}

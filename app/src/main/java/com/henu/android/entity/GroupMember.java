package com.henu.android.entity;

public class GroupMember {
    private int gid;
    private int memberId;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "gid=" + gid +
                ", memberId=" + memberId +
                '}';
    }
}

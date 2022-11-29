package com.rocketmq.acl.rocketmqaclserver.model;

public class UserInfo {
    public static final String USER_INFO = "userInfo";
    private User user;
    private long loginTime;
    private String ip;
    private String sessionId;

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "user=" + user +
                ", loginTime=" + loginTime +
                ", ip='" + ip + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}

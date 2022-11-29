package com.rocketmq.acl.rocketmqaclserver.model;

import org.hibernate.validator.constraints.Range;

public class User {
    public static final int ORDINARY = 0;
    public static final int ADMIN = 1;

    private long id;
    private String name;
    private String password;
    @Range(min = 0, max = 1)
    private int type = 0;


    public User(String name, String password, int type) {
        this.name = name;
        this.password = password;
        this.type = type;
    }

    public User cloneOne() {
        return new User(this.name, this.password, this.type);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                '}';
    }
}

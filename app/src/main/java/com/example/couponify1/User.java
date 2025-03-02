package com.example.couponify1;

import java.util.ArrayList;

public class User {

    private String id, username;
    private ArrayList<String> friends;

    public User() {
    }

    public User(String id, String username, ArrayList<String> friends) {
        this.id = id;
        this.username = username;
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public void addfriend(String friendusername) {
        friends.add(friendusername);
    }
}

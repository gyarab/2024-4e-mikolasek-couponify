package com.example.couponify1;

import java.util.ArrayList;

public class User {

    private String id, username;
    private ArrayList<User> friends;

    public User() {
    }

    public User(String id, String username, ArrayList<User> friends) {
        this.id = id;
        this.username = username;
        this.friends = friends;
    }

    public void addfriend(User friend) {
        friends.add(friend);
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

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }
}

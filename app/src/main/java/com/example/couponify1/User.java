package com.example.couponify1;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        ArrayList<String> friendlist = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("users").child(id).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemsnapshot: snapshot.getChildren()) {
                    String friend = itemsnapshot.getValue(String.class);
                    friendlist.add(friend);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return friendlist;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public void addfriend(String friendusername) {
        friends.add(friendusername);
    }
}

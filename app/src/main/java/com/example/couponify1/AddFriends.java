package com.example.couponify1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddFriends extends AppCompatActivity {

    RecyclerView addfriendsrv;
    SearchView addfriendssearch;
    List<User> userlist;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    String curuserid, curusername, querry;
    List<String> curuserfriends;
    ImageButton pendingrqbtn, friendslistbtn, inspirationtabbtn;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends);
        hideNavigationBars();

        //get current user username, id and friend list
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser authuser = mAuth.getCurrentUser();
        curuserid = authuser.getUid();
        databaseReference = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    User userr = itemSnapshot.getValue(User.class);
                    if (Objects.equals(userr.getId(), curuserid)) {
                        curusername = userr.getUsername();
                        curuserfriends = userr.getFriends();
                        //Toast.makeText(getApplicationContext(), "Logged in as " + curusername, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //initialize navigation buttons
        pendingrqbtn = findViewById(R.id.pendingrqbtn);
        pendingrqbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PendingRq.class);
                intent.putExtra("curusername", curusername);
                startActivity(intent);
                finish();
            }
        });

        friendslistbtn = findViewById(R.id.friendslistbtn);
        friendslistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                startActivity(intent);
                finish();
            }
        });
        inspirationtabbtn = findViewById(R.id.inspirationtabbtn);
        inspirationtabbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InspirationTab.class);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                startActivity(intent);
                finish();
            }
        });

        //initialize recyclerview
        addfriendsrv = findViewById(R.id.addfriendsrv);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(AddFriends.this, 1);
        addfriendsrv.setLayoutManager(gridLayoutManager);
        //start with empty list
        userlist = new ArrayList<>();

        userlistadapter adapter = new userlistadapter(AddFriends.this, userlist);
        addfriendsrv.setAdapter(adapter);

        querry = "";
        addfriendssearch = findViewById(R.id.addfriendssearch);
        addfriendssearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //get query from search bar every time there is a change
                userlist.clear();
                querry = newText;
                //display no users if the search bar is empty
                if (newText.isEmpty()) {
                    return true;
                }
                //search users for usernames that match the querry
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                            User user = itemSnapshot.getValue(User.class);
                            if (!user.getUsername().equals(curusername)) {
                                if (user.getFriends() == null || !(user.getFriends().contains(curusername)) && user.getUsername().contains(querry)) {
                                    hasrq(curusername, user.getUsername(), new HasRqCallback() {
                                        @Override
                                        public void onResult(boolean hasRequest) {
                                            if (!hasRequest && !curuserfriends.contains(user.getUsername())){
                                                //add all users who match the query to the list, update adapter
                                                userlist.add(user);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println(error);
                    }
                });
                return true;
            }
        });
    }

    public interface HasRqCallback {
        void onResult(boolean hasRequest);
    }

    //this method was generated by gemini (i was learning how to work with firebase leave me alone)
    public void hasrq(String curusername, String friend, HasRqCallback callback) {
        databaseReference.child("rq").child(friend).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> temp;
                GenericTypeIndicator<ArrayList<String>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<String>>() {};
                temp = snapshot.getValue(genericTypeIndicator);
                boolean hasRequest = false;
                if (temp != null) {
                    hasRequest = temp.contains(curusername);
                }
                //System.out.println("should be " + hasRequest);
                callback.onResult(hasRequest); // Call the callback with the result
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Database error: " + error.getMessage());
                callback.onResult(false);
            }
        });
    }
    private void hideNavigationBars() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBars();
    }
}
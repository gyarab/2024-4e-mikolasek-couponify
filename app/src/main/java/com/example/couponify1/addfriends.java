package com.example.couponify1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class addfriends extends AppCompatActivity {

    RecyclerView addfriendsrv;
    SearchView addfriendssearch;
    List<User> userlist;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    String curuserid, curusername;
    List<String> curuserfriends;
    Button searchfriendsbtn, pendingrqbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends);
        mAuth = FirebaseAuth.getInstance();
        //get current user
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
                        Toast.makeText(getApplicationContext(), "Logged in as " + curusername, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        searchfriendsbtn = findViewById(R.id.searchfriendsbtn);
        searchfriendsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), addfriends.class);
                startActivity(intent);
                finish();
            }
        });
        pendingrqbtn = findViewById(R.id.pendingrqbtn);
        pendingrqbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), pendingrq.class);
                intent.putExtra("curusername", curusername);
                startActivity(intent);
                finish();
            }
        });




        addfriendsrv = findViewById(R.id.addfriendsrv);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(addfriends.this, 1);
        addfriendsrv.setLayoutManager(gridLayoutManager);


        userlist = new ArrayList<>();

        userlistadapter adapter = new userlistadapter(addfriends.this, userlist);
        addfriendsrv.setAdapter(adapter);

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userlist.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    User user = itemSnapshot.getValue(User.class);
                    if (!user.getUsername().equals(curusername)) {
                        if (user.getFriends() == null || !(user.getFriends().contains(curusername))) {
                            hasrq(curusername, user.getUsername(), new HasRqCallback() {
                                @Override
                                public void onResult(boolean hasRequest) {
                                    if (!hasRequest){
                                        userlist.add(user);
                                        //System.out.println(userlist);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });



                            //System.out.println(hasrq(curusername, user.getUsername()));
                        }

                    }
                }
                //System.out.println("fianl "+userlist);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
    }

    public interface HasRqCallback {
        void onResult(boolean hasRequest);
    }

    //gemini
    public void hasrq(String curusername, String friend, HasRqCallback callback) {
        System.out.println("CHECKING IF " + curusername + " sent rq to " + friend);

        databaseReference.child("rq").child(friend).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("ondatachange probiha pro " + friend);
                ArrayList<String> temp = null;
                GenericTypeIndicator<ArrayList<String>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<String>>() {};
                temp = snapshot.getValue(genericTypeIndicator);
                boolean hasRequest = false;
                if (temp != null) {
                    hasRequest = temp.contains(curusername);
                }
                System.out.println("should be " + hasRequest);
                callback.onResult(hasRequest); // Call the callback with the result
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
                System.err.println("Database error: " + error.getMessage());
                callback.onResult(false); // Or handle the error in a different way
            }
        });
    }
}
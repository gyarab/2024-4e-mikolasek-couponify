package com.example.couponify1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class pendingrq extends AppCompatActivity {

    Button searchfriendsbtn2, pendingrqbtn2;

    FirebaseAuth mAuth;
    String curuserid, curusername;
    DatabaseReference databaseReference;
    List<String> curuserfriends;
    RecyclerView pendigrqrv;
    List<String> rqlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendingrq);
        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        curusername = bundle.getString("curusername");

        //get current user
        FirebaseUser authuser = mAuth.getCurrentUser();
        curuserid = authuser.getUid();

        databaseReference = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    User userr = itemSnapshot.getValue(User.class);
                    if (Objects.equals(userr.getId(), curuserid)) {
                        curuserfriends = userr.getFriends();
                        Toast.makeText(getApplicationContext(), "Logged in as " + curusername, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        pendigrqrv = findViewById(R.id.pendingrqrv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(pendingrq.this, 1);
        pendigrqrv.setLayoutManager(gridLayoutManager);

        rqlist = new ArrayList<>();

        rqlistadapter adapter = new rqlistadapter(pendingrq.this, rqlist);
        pendigrqrv.setAdapter(adapter);

        System.out.println(curusername);
        databaseReference.child("rq").child(curusername).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rqlist.clear();
                for (DataSnapshot itemsnapshot: snapshot.getChildren()) {
                    String request = itemsnapshot.getValue(String.class);
                    rqlist.add(request);
                    System.out.println(rqlist);
                    adapter.notifyDataSetChanged();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchfriendsbtn2 = findViewById(R.id.searchfriendsbtn2);
        searchfriendsbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), addfriends.class);
                startActivity(intent);
                finish();
            }
        });
        pendingrqbtn2 = findViewById(R.id.pendingrqbtn2);
        pendingrqbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), pendingrq.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
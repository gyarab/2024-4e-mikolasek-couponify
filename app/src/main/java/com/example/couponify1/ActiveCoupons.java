package com.example.couponify1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActiveCoupons extends AppCompatActivity {
    TextView hellotext, nocouponsalert;
    FirebaseAuth auth;
    String curuserid, curusername;
    ImageButton addfriendsbtn, friendsbtn, friendslistbtn, inspirationtabbtn, logoutbtn;
    RecyclerView activecouponsrv;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_coupons);
        hideNavigationBars();
        //get info from previous activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            curuserid = bundle.getString("curuserid");
            curusername = bundle.getString("curusername");
        }
        //initialize firebase auth so i can log out
        auth = FirebaseAuth.getInstance();
        //set greeting text
        hellotext = findViewById(R.id.hellotext);
        String Hello = "Hello, " + curusername;
        hellotext.setText(Hello);
        //initialize navigation buttons
        friendsbtn = findViewById(R.id.friendsbtn);
        friendsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
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
        addfriendsbtn = findViewById(R.id.addfriendsbtn);
        addfriendsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddFriends.class);
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
        //logout button terminates auth session and brings user to welcome page
        logoutbtn = findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
                finish();
            }
        });
        //initialize recyclerview
        activecouponsrv = findViewById(R.id.activecouponsrv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ActiveCoupons.this, 1);
        activecouponsrv.setLayoutManager(gridLayoutManager);
        //start with empty list
        List<Coupon> activecouponList = new ArrayList<>();
        activecouponsadapter adapter = new activecouponsadapter(ActiveCoupons.this, activecouponList);
        activecouponsrv.setAdapter(adapter);
        nocouponsalert = findViewById(R.id.nocouponsalert);
        //get users active coupons from database
        databaseReference = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("users").child(curuserid).child("activecoupons").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemsnapshot: snapshot.getChildren()) {
                    Coupon coupon = itemsnapshot.getValue(Coupon.class);
                    if (coupon != null) {
                        activecouponList.add(coupon);
                    }
                }
                //update adapter
                adapter.notifyDataSetChanged();
                //show text if there are no active coupons
                if (activecouponList.isEmpty()) {
                    nocouponsalert.setVisibility(View.VISIBLE);
                } else {nocouponsalert.setVisibility(View.GONE);}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
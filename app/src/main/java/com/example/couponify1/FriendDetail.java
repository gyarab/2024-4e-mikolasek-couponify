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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendDetail extends AppCompatActivity {

    TextView textview, nocouponsalert;
    RecyclerView couponlistrv;
    ImageButton friendslistbtn, addfriendsbtn, inspirationtabbtn, gamecouponsbtn;
    FloatingActionButton writecouponbtn;
    List<Coupon> Couponlist;
    DatabaseReference databaseReference;
    String curusername, curuserid, selectedfriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frienddetail);
        hideNavigationBars();
        Bundle bundle = getIntent().getExtras();
        selectedfriend = bundle.getString("selectedfriend");
        curusername = bundle.getString("curusername");
        curuserid = bundle.getString("curuserid");
        textview = findViewById(R.id.textView);
        textview.setText("Coupons received from "+selectedfriend);
        writecouponbtn = findViewById(R.id.writecouponbtn);
        writecouponbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WriteNewCoupon.class);
                intent.putExtra("selectedfriend", selectedfriend);
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
        gamecouponsbtn = findViewById(R.id.gamecouponsbtn);
        gamecouponsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameCoupons.class);
                intent.putExtra("selectedfriend", selectedfriend);
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

        nocouponsalert = findViewById(R.id.nocouponsalert);
        couponlistrv = findViewById(R.id.couponlistrv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(FriendDetail.this, 1);
        couponlistrv.setLayoutManager(gridLayoutManager);

        Couponlist = new ArrayList<>();

        Couponlistadapter adapter = new Couponlistadapter(FriendDetail.this, Couponlist);
        couponlistrv.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("users").child(curuserid).child("coupons").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemsnapshot: snapshot.getChildren()) {
                    Coupon coupon = itemsnapshot.getValue(Coupon.class);
                    if (coupon != null && coupon.getWrittenby().equals(selectedfriend)) {
                        Couponlist.add(coupon);
                    }
                }
                adapter.notifyDataSetChanged();
                if (Couponlist.isEmpty()) {
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
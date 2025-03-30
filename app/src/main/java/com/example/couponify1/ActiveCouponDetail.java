package com.example.couponify1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ActiveCouponDetail extends AppCompatActivity {
    DatabaseReference mDatabase;
    String coupontitle, coupondate, coupondesc, writtenby, curusername, curuserid;
    TextView title, date, desc;
    ImageButton backbtn, friendslistbtn, inspirationtabbtn, addfriendsbtn;
    Button dismissbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activecoupondetail);
        hideNavigationBars();
        //get information from previous activity
        Bundle bundle = getIntent().getExtras();
        coupontitle = bundle.getString("coupontitle");
        coupondate = bundle.getString("coupondate");
        coupondesc = bundle.getString("coupondesc");
        writtenby = bundle.getString("writtenby");
        curusername = bundle.getString("curusername");
        curuserid = bundle.getString("curuserid");
        //initialize database
        mDatabase = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        //set correct text into textfileds
        title = findViewById(R.id.activecoupondetailtitle);
        title.setText(coupontitle);
        date = findViewById(R.id.activecoupondetaildate);
        date.setText("received on: " + coupondate);
        desc = findViewById(R.id.activecoupondetaildesc);
        desc.setText(coupondesc);

        //initialize navigation buttons
        friendslistbtn = findViewById(R.id.friendslistbtn);
        friendslistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                startActivity(intent);
                finish();
            }
        });
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActiveCoupons.class);
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
        //dismiss button clears the coupon from active coupons, both current user and friend who wrote the coupon
        dismissbtn = findViewById(R.id.dismissbtn);
        dismissbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get current users active coupons
                mDatabase.child("users").child(curuserid).child("activecoupons").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        //go through all active coupons and delete the one that matches the currently selected coupon
                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                            Coupon cpn = itemsnapshot.getValue(Coupon.class);
                            if (Objects.equals(cpn.getTitle(), coupontitle) && Objects.equals(cpn.getDesc(), coupondesc)) {
                                mDatabase.child("users").child(curuserid).child("activecoupons").child(itemsnapshot.getKey()).removeValue();
                            }
                        }
                    }
                });
                //find id of user that wrote the coupon
                mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                            User userr = itemsnapshot.getValue(User.class);
                            if (Objects.equals(userr.getUsername(), writtenby)) {
                                String writtenbyid = userr.getId();
                                //now remove the coupon for this user
                                mDatabase.child("users").child(writtenbyid).child("activecoupons").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                                            Coupon cpn = itemsnapshot.getValue(Coupon.class);
                                            if (Objects.equals(cpn.getTitle(), coupontitle) && Objects.equals(cpn.getDesc(), coupondesc)) {
                                                System.out.println("key " + itemsnapshot.getKey());
                                                mDatabase.child("users").child(writtenbyid).child("activecoupons").child(itemsnapshot.getKey()).removeValue();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
        //dismiss button shows up only for the one who received the coupon, not the one who wrote it
        if (Objects.equals(curusername, writtenby)) {
            dismissbtn.setVisibility(View.GONE);
        }
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
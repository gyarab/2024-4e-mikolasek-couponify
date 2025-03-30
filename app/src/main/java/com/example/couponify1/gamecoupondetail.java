package com.example.couponify1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class gamecoupondetail extends AppCompatActivity {
    DatabaseReference mDatabase;
    String gamecoupontitle, gamecoupondesc, writtenby, curusername, curuserid, writtento, friendid, friendname;
    TextView title, desc;
    Button redeembtn;
    ImageButton backbtn, friendslistbtn, addfriendsbtn, inspirationtabbtn;
    Boolean isactive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamecoupondetail);
        hideNavigationBars();
        Bundle bundle = getIntent().getExtras();
        gamecoupontitle = bundle.getString("coupontitle");
        gamecoupondesc = bundle.getString("coupondesc");
        writtenby = bundle.getString("writtenby");
        writtento = bundle.getString("writtento");
        curusername = bundle.getString("curusername");
        curuserid = bundle.getString("curuserid");
        isactive = bundle.getBoolean("isactive");
        mDatabase = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        title = findViewById(R.id.coupondetailtitle);
        title.setText(gamecoupontitle);
        desc = findViewById(R.id.coupondetaildesc);
        desc.setText(gamecoupondesc);

        redeembtn = findViewById(R.id.redeembtn);
        if (!isactive) {
            redeembtn.setVisibility(View.GONE);
        }
        redeembtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (Objects.equals(writtenby, curusername)) {
                    friendname = writtento;
                } else {
                    friendname = writtenby;
                }
                GameCoupon coupon = new GameCoupon(gamecoupontitle, gamecoupondesc, curusername, friendname);
                mDatabase.child("users").child(curuserid).child("activecoupons").push().setValue(coupon);
                mDatabase.child("users").child(curuserid).child("gamecoupons").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                            GameCoupon cpn = itemsnapshot.getValue(GameCoupon.class);
                            if (Objects.equals(cpn.getTitle(), coupon.getTitle()) && Objects.equals(cpn.getDesc(), coupon.getDesc())) {
                                itemsnapshot.getRef().removeValue();
                            }
                        }
                    }
                });
                mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                            User userr = itemsnapshot.getValue(User.class);
                            if (userr.getUsername().equals(friendname)) {
                                friendid = userr.getId();
                                mDatabase.child("users").child(friendid).child("activecoupons").push().setValue(coupon);
                                sendNotifWithID("Coupon redeemed!", curusername + " has redeemed a coupon from you, time to deliver on your promise!", friendid);
                            }
                        }
                    }
                });
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
                Intent intent = new Intent(getApplicationContext(), addfriends.class);
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
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), gamecoupons.class);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                if (Objects.equals(writtenby, curusername)) {
                    intent.putExtra("selectedfriend", writtento);
                } else {
                    intent.putExtra("selectedfriend", writtenby);
                }
                startActivity(intent);
                finish();
            }
        });
    }
    public void sendNotifWithID(String title, String desc, String friendid){
        mDatabase.child("Tokens").child(friendid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String token = task.getResult().getValue(String.class);
                final ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        SendNotification sn = new SendNotification();
                        sn.SendPushNotification(title, desc, token);
                    }
                });
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
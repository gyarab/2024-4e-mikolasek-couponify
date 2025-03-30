package com.example.couponify1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WriteNewGameCoupon extends AppCompatActivity {
    String selectedfriend, selectedfriendid, curusername, curuserid;
    TextView textview2;
    TextInputEditText edittexttitle, edittextdesc;
    Button sendcouponbtn;
    private DatabaseReference mDatabase;
    ImageButton backbtn, friendslistbtn, inspirationtabbtn, addfriendsbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writenewgamecoupon);
        hideNavigationBars();
        Bundle bundle = getIntent().getExtras();
        selectedfriend = (String) bundle.get("selectedfriend");
        curusername = bundle.getString("curusername");
        curuserid = bundle.getString("curuserid");
        mDatabase = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        textview2 = findViewById(R.id.textView2);
        textview2.setText("Write a new game coupon for " + selectedfriend);

        mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot s: task.getResult().getChildren()) {
                    User userr = s.getValue(User.class);
                    if (userr.getUsername().equals(selectedfriend)) {
                        selectedfriendid = userr.getId();
                        break;
                    }
                }
            }
        });

        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameCoupons.class);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                intent.putExtra("selectedfriend", selectedfriend);
                startActivity(intent);
                finish();
            }
        });
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
        sendcouponbtn = findViewById(R.id.sendcouponbtn);
        edittexttitle = findViewById(R.id.edittexttitle);
        edittextdesc = findViewById(R.id.edittextdesc);
        sendcouponbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coupontitle = String.valueOf(edittexttitle.getText());
                String coupondesc = String.valueOf(edittextdesc.getText());
                if (coupontitle.isEmpty()) {
                    Toast.makeText(WriteNewGameCoupon.this, "Enter coupon title.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (coupondesc.isEmpty()) {
                    Toast.makeText(WriteNewGameCoupon.this, "Enter coupon description.", Toast.LENGTH_SHORT).show();
                    return;
                }
                GameCoupon coupon = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    coupon = new GameCoupon(coupontitle, coupondesc, curusername, selectedfriend);
                }
                mDatabase.child("users").child(selectedfriendid).child("gamecoupons").push().setValue(coupon);
                mDatabase.child("users").child(curuserid).child("gamecoupons").push().setValue(coupon);
                sendNotifWithID("New game coupon received!", "you have received a new game coupon from " + curusername, selectedfriendid);
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
}
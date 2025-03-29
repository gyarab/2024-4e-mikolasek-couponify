package com.example.couponify1;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class writenewcoupon extends AppCompatActivity {

    String selectedfriend, selectedfriendid, curusername;
    TextView textview2;
    EditText edittexttitle, edittextdesc;
    Button sendcouponbtn;
    private DatabaseReference mDatabase;
    List<Coupon> selectedfriendcoupons;
    ImageButton backbtn, friendslistbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writenewcoupon);
        hideNavigationBars();
        Bundle bundle = getIntent().getExtras();
        selectedfriend = (String) bundle.get("selectedfriend");
        mDatabase = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot s: task.getResult().getChildren()) {
                    User userr = s.getValue(User.class);
                    if (userr.getUsername().equals(selectedfriend)) {
                        selectedfriendid = userr.getId();
                        mDatabase.child("users").child(selectedfriendid).child("coupons").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                GenericTypeIndicator<List<Coupon>> gti = new GenericTypeIndicator<List<Coupon>>() {};
                                selectedfriendcoupons = task.getResult().getValue(gti);
                                System.out.println(selectedfriendcoupons);
                            }
                        });
                        break;
                    }
                }
            }
        });
        curusername = (String) bundle.get("curusername");
        textview2 = findViewById(R.id.textView2);
        textview2.setText("Write a new coupon for " + selectedfriend);

        friendslistbtn = findViewById(R.id.friendslistbtn);
        friendslistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendcouponbtn = findViewById(R.id.sendcouponbtn);
        edittexttitle = findViewById(R.id.edittexttitle);
        edittextdesc = findViewById(R.id.edittextdesc);
        sendcouponbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String coupontitle = String.valueOf(edittexttitle.getText());
                String coupondesc = String.valueOf(edittextdesc.getText());
                Coupon coupon = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    coupon = new Coupon(coupontitle, coupondesc, curusername, selectedfriend);
                }
                if (selectedfriendcoupons == null || selectedfriendcoupons.isEmpty()) {
                    List<Coupon> list = new ArrayList<>();
                    list.add(coupon);
                    mDatabase.child("users").child(selectedfriendid).child("coupons").setValue(list);
                } else {
                    selectedfriendcoupons.add(coupon);
                    mDatabase.child("users").child(selectedfriendid).child("coupons").setValue(selectedfriendcoupons);
                }
                sendNotifWithID("New coupon received!", "you have received a new coupon from " + curusername, selectedfriendid);
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
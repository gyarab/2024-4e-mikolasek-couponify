package com.example.couponify1;

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

public class coupondetail extends AppCompatActivity {
    DatabaseReference mDatabase;
    String coupontitle, coupondate, coupondesc, writtenby, curusername, curuserid;
    TextView title, date, desc;
    Button redeembtn;
    ImageButton backbtn, friendslistbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupondetail);
        hideNavigationBars();
        Bundle bundle = getIntent().getExtras();
        coupontitle = bundle.getString("coupontitle");
        coupondate = bundle.getString("coupondate");
        coupondesc = bundle.getString("coupondesc");
        writtenby = bundle.getString("writtenby");
        curusername = bundle.getString("curusername");
        curuserid = bundle.getString("curuserid");
        mDatabase = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        title = findViewById(R.id.coupondetailtitle);
        title.setText(coupontitle);
        date = findViewById(R.id.coupondetaildate);
        date.setText("received on: " + coupondate);
        desc = findViewById(R.id.coupondetaildesc);
        desc.setText(coupondesc);
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
        redeembtn = findViewById(R.id.redeembtn);
        redeembtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Coupon coupon = new Coupon(coupontitle, coupondesc, writtenby, curusername);
                coupon.setCreatedon(coupondate);
                mDatabase.child("users").child(curuserid).child("activecoupons").push().setValue(coupon);
                mDatabase.child("users").child(curuserid).child("coupons").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                            Coupon cpn = itemsnapshot.getValue(Coupon.class);
                            if (Objects.equals(cpn.getTitle(), coupon.getTitle()) && Objects.equals(cpn.getDesc(), coupon.getDesc())) {
                                System.out.println("key "+itemsnapshot.getKey());
                                mDatabase.child("users").child(curuserid).child("coupons").child(itemsnapshot.getKey()).removeValue();
                            }
                        }
                    }
                });
                mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                            User userr = itemsnapshot.getValue(User.class);
                            if (userr.getUsername().equals(writtenby)) {
                                mDatabase.child("users").child(userr.getId()).child("activecoupons").push().setValue(coupon).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        System.out.println("added to active");
                                    }
                                });
                            }
                        }
                    }
                });
                sendNotifWithUsername("Coupon redeemed!", curusername + " has redeemed a coupon from you, time to deliver on your promise!", writtenby);
            }
        });
    }

    public void sendNotifWithUsername(String title, String desc, String friendusername){
        mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                    User userr = itemsnapshot.getValue(User.class);
                    if (Objects.equals(userr.getUsername(), friendusername)) {
                        String friendid = userr.getId();
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
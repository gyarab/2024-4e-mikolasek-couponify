package com.example.couponify1;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    String coupontitle, coupondate, coupondesc, writtenby, curusername;
    TextView title, date, desc;
    Button redeembtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupondetail);
        Bundle bundle = getIntent().getExtras();
        coupontitle = bundle.getString("coupontitle");
        coupondate = bundle.getString("coupondate");
        coupondesc = bundle.getString("coupondesc");
        writtenby = bundle.getString("writtenby");
        curusername = bundle.getString("curusername");
        mDatabase = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        title = findViewById(R.id.coupondetailtitle);
        title.setText(coupontitle);
        date = findViewById(R.id.coupondetaildate);
        date.setText(coupondate);
        desc = findViewById(R.id.coupondetaildesc);
        desc.setText(coupondesc);
        redeembtn = findViewById(R.id.redeembtn);
        redeembtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                            User userr = itemsnapshot.getValue(User.class);
                            if (userr.getUsername().equals(writtenby) || userr.getUsername().equals(curusername)) {
                                Coupon coupon = new Coupon(coupontitle, coupondesc, writtenby, curusername);
                                coupon.setCreatedon(coupondate);
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
}
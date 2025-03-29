package com.example.couponify1;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class activecoupondetail extends AppCompatActivity {
    DatabaseReference mDatabase;
    String coupontitle, coupondate, coupondesc, writtenby, curusername, curuserid;
    TextView title, date, desc;
    ImageButton backbtn, friendslistbtn;
    Button dismissbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activecoupondetail);
        Bundle bundle = getIntent().getExtras();
        hideNavigationBars();
        coupontitle = bundle.getString("coupontitle");
        coupondate = bundle.getString("coupondate");
        coupondesc = bundle.getString("coupondesc");
        writtenby = bundle.getString("writtenby");
        curusername = bundle.getString("curusername");
        curuserid = bundle.getString("curuserid");
        mDatabase = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        title = findViewById(R.id.activecoupondetailtitle);
        title.setText(coupontitle);
        date = findViewById(R.id.activecoupondetaildate);
        date.setText("received on: " + coupondate);
        desc = findViewById(R.id.activecoupondetaildesc);
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
        dismissbtn = findViewById(R.id.dismissbtn);
        dismissbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("users").child(curuserid).child("activecoupons").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                            Coupon cpn = itemsnapshot.getValue(Coupon.class);
                            if (Objects.equals(cpn.getTitle(), coupontitle) && Objects.equals(cpn.getDesc(), coupondesc)) {
                                mDatabase.child("users").child(curuserid).child("activecoupons").child(itemsnapshot.getKey()).removeValue();
                            }
                        }
                    }
                });
                mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                            User userr = itemsnapshot.getValue(User.class);
                            if (Objects.equals(userr.getUsername(), writtenby)) {
                                String writtenbyid = userr.getId();
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
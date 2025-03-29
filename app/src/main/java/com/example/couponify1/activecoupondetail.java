package com.example.couponify1;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class activecoupondetail extends AppCompatActivity {
    DatabaseReference mDatabase;
    String coupontitle, coupondate, coupondesc, writtenby, curusername;
    TextView title, date, desc;
    boolean writtentome;
    ImageButton backbtn, friendslistbtn;

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
        writtentome = bundle.getBoolean("writtentome");
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
package com.example.couponify1;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activecoupondetail);
        Bundle bundle = getIntent().getExtras();
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
        date.setText(coupondate);
        desc = findViewById(R.id.activecoupondetaildesc);
        desc.setText(coupondesc);
    }
}
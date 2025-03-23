package com.example.couponify1;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class coupondetail extends AppCompatActivity {

    String coupontitle, coupondate, coupondesc;
    TextView title, date, desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupondetail);
        Bundle bundle = getIntent().getExtras();
        coupontitle = bundle.getString("coupontitle");
        coupondate = bundle.getString("coupondate");
        coupondesc = bundle.getString("coupondesc");

        title = findViewById(R.id.coupondetailtitle);
        title.setText(coupontitle);
        date = findViewById(R.id.coupondetaildate);
        date.setText(coupondate);
        desc = findViewById(R.id.coupondetaildesc);
        desc.setText(coupondesc);
    }
}
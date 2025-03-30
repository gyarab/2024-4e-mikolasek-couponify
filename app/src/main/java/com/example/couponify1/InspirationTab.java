package com.example.couponify1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class InspirationTab extends AppCompatActivity {
    ImageButton friendslistbtn, addfriendsbtn, favoritesbtn;
    String curuserid, curusername;
    RecyclerView insporv;
    List<InspoCoupon> inspocouponlist, favlist;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspiration_tab);
        hideNavigationBars();
        Bundle bundle = getIntent().getExtras();
        curusername = bundle.getString("curusername");
        curuserid = bundle.getString("curuserid");

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
        favoritesbtn = findViewById(R.id.favoritesbtn);
        favoritesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), inspiration_favorites.class);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                startActivity(intent);
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        insporv = findViewById(R.id.insporv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(InspirationTab.this, 1);
        insporv.setLayoutManager(gridLayoutManager);

        inspocouponlist = new ArrayList<>();
        favlist = new ArrayList<>();


        databaseReference.child("users").child(curuserid).child("favoritedinspo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                    InspoCoupon c = itemsnapshot.getValue(InspoCoupon.class);
                    favlist.add(c);
                }
                databaseReference.child("Inspo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                            InspoCoupon c = itemsnapshot.getValue(InspoCoupon.class);
                            Boolean contains = false;
                            for (InspoCoupon i : favlist) {
                                if (Objects.equals(c.title, i.title)) {
                                    contains = true;
                                }
                            }
                            if (!contains) {
                                inspocouponlist.add(c);
                            }
                        }
                        List<InspoCoupon> temp = sortbyliked(inspocouponlist, favlist);
                        inspocouponlist = temp;
                        inspoadapter adapter = new inspoadapter(InspirationTab.this, inspocouponlist);
                        insporv.setAdapter(adapter);
                    }
                });
            }
        });
    }

    private List<InspoCoupon> sortbyliked(List<InspoCoupon> inspocouponlist, List<InspoCoupon> favlist) {
        int numofcouple = 0;
        int numoffamily = 0;
        int numoffriends = 0;
        int numofwork = 0;
        for (InspoCoupon i: favlist) {
            if (Objects.equals(i.getGenre(), "couple")) {
                numofcouple++;
            }
            if (Objects.equals(i.getGenre(), "family")) {
                numoffamily++;
            }
            if (Objects.equals(i.getGenre(), "friends")) {
                numoffriends++;
            }
            if (Objects.equals(i.getGenre(), "work")) {
                numofwork++;
            }
        }
        String mostliked = "couple";
        if (numoffriends > numofcouple) {
            mostliked = "friends";
        }
        if (numoffamily > numoffriends) {
            mostliked = "family";
        }
        if (numofwork > numoffamily) {
            mostliked = "work";
        }
        List<InspoCoupon> temp = new ArrayList<>();
        for (InspoCoupon coupon : inspocouponlist) {
            if (Objects.equals(coupon.getGenre(), mostliked)) {
                temp.add(coupon);
            }
        }
        for (InspoCoupon coupon : inspocouponlist) {
            if (!Objects.equals(coupon.getGenre(), mostliked)) {
                temp.add(coupon);
            }
        }
        return temp;
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
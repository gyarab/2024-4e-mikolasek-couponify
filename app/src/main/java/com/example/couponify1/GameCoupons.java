package com.example.couponify1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameCoupons extends AppCompatActivity {
    ImageButton friendslistbtn, addfriendsbtn, inspirationtabbtn, receivedcouponsbtn, writecouponbtn, startgamebtn;
    String curusername, curuserid, selectedfriend;
    TextView textView, nocouponsalert, active_inactive;
    RecyclerView gamecouponlistrv;
    List<GameCoupon> Gamecouponlist;
    DatabaseReference databaseReference;
    ConstraintLayout controls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamecoupons);
        hideNavigationBars();
        Bundle bundle = getIntent().getExtras();
        selectedfriend = bundle.getString("selectedfriend");
        curusername = bundle.getString("curusername");
        curuserid = bundle.getString("curuserid");

        textView = findViewById(R.id.textView);
        textView.setText("Game coupons shared with " + selectedfriend);
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
        receivedcouponsbtn = findViewById(R.id.receivedcouponsbtn);
        receivedcouponsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FriendDetail.class);
                intent.putExtra("selectedfriend", selectedfriend);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                startActivity(intent);
                finish();
            }
        });
        startgamebtn = findViewById(R.id.startgamebtn);
        startgamebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StartGameSession.class);
                intent.putExtra("selectedfriend", selectedfriend);
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
        writecouponbtn = findViewById(R.id.writecouponbtn);
        writecouponbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WriteNewGameCoupon.class);
                intent.putExtra("selectedfriend", selectedfriend);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                startActivity(intent);
                finish();
            }
        });

        controls = findViewById(R.id.controls);
        active_inactive = findViewById(R.id.active_inactive);
        nocouponsalert = findViewById(R.id.nocouponsalert);
        gamecouponlistrv = findViewById(R.id.gamecouponlistrv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(GameCoupons.this, 1);
        gamecouponlistrv.setLayoutManager(gridLayoutManager);

        Gamecouponlist = new ArrayList<>();

        Gamecouponlistadapter adapter = new Gamecouponlistadapter(GameCoupons.this, Gamecouponlist);
        gamecouponlistrv.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("users").child(curuserid).child("gamecoupons").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemsnapshot: snapshot.getChildren()) {
                    GameCoupon coupon = itemsnapshot.getValue(GameCoupon.class);
                    if (coupon != null
                            && ((Objects.equals(coupon.getWrittenby(), curusername) && Objects.equals(coupon.getWrittento(), selectedfriend))
                            || (Objects.equals(coupon.getWrittenby(), selectedfriend) && Objects.equals(coupon.getWrittento(), curusername)))) {
                        Gamecouponlist.add(coupon);
                    }
                }
                adapter.notifyDataSetChanged();
                if (Gamecouponlist.isEmpty()) {
                    databaseReference.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                                User user = itemsnapshot.getValue(User.class);
                                if (Objects.equals(user.getUsername(), selectedfriend)) {
                                    databaseReference.child("users").child(user.getId()).child("gamecoupons").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                                                GameCoupon coupon = itemsnapshot.getValue(GameCoupon.class);
                                                if (coupon != null
                                                        && ((Objects.equals(coupon.getWrittenby(), curusername) && Objects.equals(coupon.getWrittento(), selectedfriend))
                                                        || (Objects.equals(coupon.getWrittenby(), selectedfriend) && Objects.equals(coupon.getWrittento(), curusername)))) {
                                                    nocouponsalert.setText(selectedfriend + " still has some game coupons!");
                                                    active_inactive.setText(R.string.game_session_active);
                                                    controls.setVisibility(View.GONE);
                                                    break;
                                                }
                                            }
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    });
                    nocouponsalert.setVisibility(View.VISIBLE);
                    active_inactive.setText(R.string.game_session_inactive);
                } else {
                    GameCoupon coupon = Gamecouponlist.get(0);
                    if (coupon.isactive) {
                        active_inactive.setText(R.string.game_session_active);
                        controls.setVisibility(View.GONE);
                    } else {
                        active_inactive.setText(R.string.game_session_inactive);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
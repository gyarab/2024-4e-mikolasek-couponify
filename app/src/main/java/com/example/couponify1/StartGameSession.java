package com.example.couponify1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class StartGameSession extends AppCompatActivity {
    String selectedfriend,selectedfriendid, curusername, curuserid;
    ImageButton backbtn, friendslistbtn, addfriendsbtn, inspirationtabbtn;
    TextView startgametitle, numcoupons1, numcoupons2, oddwarning, zerowarning;
    int couponsfromcuruser, couponsfromfriend;
    Button startgamebtn;
    DatabaseReference mDatabase;
    List<GameCoupon> list, randlist1, randlist2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startgamesession);
        hideNavigationBars();
        Bundle bundle = getIntent().getExtras();
        curusername = bundle.getString("curusername");
        curuserid = bundle.getString("curuserid");
        selectedfriend = bundle.getString("selectedfriend");
        //initialize everything
        startgametitle = findViewById(R.id.startgametitle);
        startgametitle.setText("Start game session with " + selectedfriend);
        mDatabase = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        numcoupons1 = findViewById(R.id.numcoupons1);
        numcoupons2 = findViewById(R.id.numcoupons2);
        couponsfromcuruser = 0;
        couponsfromfriend = 0;
        oddwarning = findViewById(R.id.oddwarning);
        zerowarning = findViewById(R.id.zerowarning);
        startgamebtn = findViewById(R.id.startgamebtn);
        //this list is for both game coupons that current user wrote and those that selected friend wrote, they are shared
        list = new ArrayList<>();
        mDatabase.child("users").child(curuserid).child("gamecoupons").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                    GameCoupon coupon = itemsnapshot.getValue(GameCoupon.class);
                    //count how many are from current user and how many from friend
                    if (Objects.equals(coupon.getWrittento(), curusername) && Objects.equals(coupon.getWrittenby(), selectedfriend)) {
                        couponsfromfriend++;
                        list.add(coupon);
                    }
                    if (Objects.equals(coupon.getWrittenby(), curusername) && Objects.equals(coupon.getWrittento(), selectedfriend)) {
                        couponsfromcuruser++;
                        list.add(coupon);
                    }
                }
                numcoupons1.setText("Coupons written by you: " + couponsfromcuruser);
                numcoupons2.setText("Coupons written by " + selectedfriend + ": " + couponsfromfriend);
                //show text if there are no game coupons or the number of them is odd, game session cannot be started then
                if (list.isEmpty()) {
                    zerowarning.setVisibility(View.VISIBLE);
                    startgamebtn.setVisibility(View.GONE);
                } else if (list.size()%2 != 0) {
                    oddwarning.setVisibility(View.VISIBLE);
                    startgamebtn.setVisibility(View.GONE);
                } else {
                    startgamebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //shuffle the list randomly
                            Collections.shuffle(list, new Random());
                            randlist1 = new ArrayList<>();
                            randlist2 = new ArrayList<>();
                            //give first half to current user, second to friend
                            randlist1.addAll(list.subList(0,list.size()/2));
                            randlist2.addAll(list.subList(list.size()/2, list.size()));
                            mDatabase.child("users").child(curuserid).child("gamecoupons").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                                        GameCoupon coupon = itemsnapshot.getValue(GameCoupon.class);
                                        for (int i = 0; i < randlist1.size(); i++) {
                                            GameCoupon c = randlist1.get(i);
                                            if (coupon != null && Objects.equals(c.title, coupon.title) && Objects.equals(c.desc, coupon.desc) && Objects.equals(c.writtenby, coupon.writtenby)) {
                                                //activate the game coupons that are supposed to stay
                                                itemsnapshot.getRef().removeValue();
                                                coupon.activate();
                                                itemsnapshot.getRef().setValue(coupon);
                                            }
                                        }
                                        //remove the coupons that belong to friend
                                        for (int i = 0; i < randlist2.size(); i++) {
                                            GameCoupon c = randlist2.get(i);
                                            if (coupon != null && Objects.equals(c.title, coupon.title) && Objects.equals(c.desc, coupon.desc) && Objects.equals(c.writtenby, coupon.writtenby)) {
                                                itemsnapshot.getRef().removeValue();
                                            }
                                        }
                                    }
                                }
                            });
                            //get friend id
                            mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                                        User user = itemsnapshot.getValue(User.class);
                                        if (Objects.equals(user.getUsername(), selectedfriend)) {
                                            selectedfriendid = user.getId();
                                            break;
                                        }
                                    }
                                    //now do the same for firend
                                    mDatabase.child("users").child(selectedfriendid).child("gamecoupons").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                                                GameCoupon coupon = itemsnapshot.getValue(GameCoupon.class);
                                                for (int i = 0; i < randlist1.size(); i++) {
                                                    GameCoupon c = randlist1.get(i);
                                                    if (coupon != null && Objects.equals(c.title, coupon.title) && Objects.equals(c.desc, coupon.desc) && Objects.equals(c.writtenby, coupon.writtenby)) {
                                                        itemsnapshot.getRef().removeValue();
                                                    }
                                                }
                                                for (int i = 0; i < randlist2.size(); i++) {
                                                    GameCoupon c = randlist2.get(i);
                                                    if (coupon != null && Objects.equals(c.title, coupon.title) && Objects.equals(c.desc, coupon.desc) && Objects.equals(c.writtenby, coupon.writtenby)) {
                                                        itemsnapshot.getRef().removeValue();
                                                        coupon.activate();
                                                        itemsnapshot.getRef().setValue(coupon);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
        //uninteresting navigation buttons
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
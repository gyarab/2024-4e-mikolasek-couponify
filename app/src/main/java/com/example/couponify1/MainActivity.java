package com.example.couponify1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Boolean notifpermgranted;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    ImageButton addfriendsbtn, logoutbtn, activecouponsbtn, inspirationtabbtn;
    String curuserid, curusername, curusertoken;
    TextView hellotext;
    RecyclerView friendslistrv;

    //ask permission to send notifications
    private ActivityResultLauncher <String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted){
            notifpermgranted = true;
            getDeviceToken();
        } else {
            //notif not granted
        }
    });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideNavigationBars();
        requestNotifPermission();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            curuserid = bundle.getString("curuserid");
            curusername = bundle.getString("curusername");
        }
        //failsafe for null user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null || curuserid == null) {
            Intent intent = new Intent(getApplicationContext(), Welcome.class);
            startActivity(intent);
            finish();
        }
        hellotext = findViewById(R.id.hellotext);
        String Hello = "Hello, " + curusername;
        hellotext.setText(Hello);

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

        friendslistrv = findViewById(R.id.friendslistrv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        friendslistrv.setLayoutManager(gridLayoutManager);

        List<String> friendslist = new ArrayList<>();

        friendslistadapter adapter = new friendslistadapter(MainActivity.this, friendslist);
        friendslistrv.setAdapter(adapter);

        //gets friends from database
        databaseReference = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("users").child(curuserid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    String friend = (String) itemSnapshot.getValue();
                    friendslist.add(friend);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        activecouponsbtn = findViewById(R.id.activecouponsbtn);
        activecouponsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActiveCoupons.class);
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
        //terminates auth session
        logoutbtn = findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
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

    public void requestNotifPermission () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notifpermgranted = true;
                getDeviceToken();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(MainActivity.this, "Notifications disabled, please enable them to get the full experience.",
                        Toast.LENGTH_LONG).show();
            } else {
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            getDeviceToken();
        }
    }

    public void getDeviceToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    System.out.println("Fetching token failed " + task.getException());
                    return;
                }
                curusertoken = task.getResult();
                databaseReference.child("Tokens").child(curuserid).setValue(curusertoken);
            }
        });
    }
}
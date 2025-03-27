package com.example.couponify1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    Boolean notifpermgranted;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    Button back_btn;
    ImageButton addfriendsbtn;
    String curuserid, curusername, curusertoken;
    TextView hellotext;
    RecyclerView friendslistrv;

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
            curuserid = bundle.getString("id");
            curusername = bundle.getString("username");
        }
        //failsafe for null user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null || curuserid == null) {
            Intent intent = new Intent(getApplicationContext(), welcome.class);
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
                Intent intent = new Intent(getApplicationContext(), addfriends.class);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                startActivity(intent);
                sendNotifFull("Friend request received!", "NAME has sent you a friend request.", "fU7_rCbcS9CIQIt22ANzeG:APA91bGtLN4ULvGPNqGVvIyiEP8z4AkeinZdmZNSWVU0a0oMFIf4fSAXFFQ-GER8WnQPluvVsCoVb0py42mPmBUm3nIaXCNEyvExW0BmuRanTyNpLyzjhwI");
                //finish();
            }
        });

        friendslistrv = findViewById(R.id.friendslistrv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        friendslistrv.setLayoutManager(gridLayoutManager);

        List<String> friendslist = new ArrayList<>();

        friendslistadapter adapter = new friendslistadapter(MainActivity.this, friendslist);
        friendslistrv.setAdapter(adapter);

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

        back_btn = findViewById(R.id.backbtn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), welcome.class);
                boolean logout = true;
                intent.putExtra("logout", logout);
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
                //TODO: show info about notif permission
                System.out.println("notifs disabled :(");
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
                    Log.e("logs", " Fetching token failed " + task.getException());
                    return;
                }
                curusertoken = task.getResult();
                System.out.println("device token: " + curusertoken);
                databaseReference.child("Tokens").child(curuserid).setValue(curusertoken);
            }
        });
    }

    public void sendNotifFull(String title, String desc, String token){
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                SendNotification sn = new SendNotification();
                sn.SendPushNotification(title, desc, token);
            }
        });
    }

}
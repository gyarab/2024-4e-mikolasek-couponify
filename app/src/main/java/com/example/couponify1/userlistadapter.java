package com.example.couponify1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class userlistadapter extends RecyclerView.Adapter<userlistholder> {
    private Context context;
    private List<User> userlist;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String curuserid, curusername;

    public userlistadapter(Context context, List<User> userlist) {
        this.context = context;
        this.userlist = userlist;
    }

    @NonNull
    @Override
    public userlistholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        findCuruser();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_listitem, parent, false);
        return new userlistholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userlistholder holder, int position) {
        holder.useritemusername.setText(userlist.get(position).getUsername());
        holder.addfriendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendusername = userlist.get(holder.getAdapterPosition()).getUsername();
                createfriendrequest(friendusername);
            }
        });
    }

    private void createfriendrequest(String friendusername) {
        mDatabase.child("rq").child(friendusername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    List<String> rqlist = (List<String>) snapshot.getValue();
                    if (!rqlist.contains(curusername)) {
                        rqlist.add(curusername);
                        mDatabase.child("rq").child(friendusername).setValue(rqlist);
                        sendNotifFull("Friend request received!", curusername + " has sent you a friend request.", friendusername);
                        Toast.makeText(context, "Friend request sent.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    List<String> rqlist = new ArrayList<>();
                    rqlist.add(curusername);
                    mDatabase.child("rq").child(friendusername).setValue(rqlist);

                    Toast.makeText(context, "Friend request sent.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public void searchusers(String searchedname){

    }
    private void findCuruser() {
        mDatabase = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser authuser = mAuth.getCurrentUser();
        curuserid = authuser.getUid();

        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    User userr = itemSnapshot.getValue(User.class);
                    if (Objects.equals(userr.getId(), curuserid)) {
                        curusername = userr.getUsername();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void sendNotifFull(String title, String desc, String friendusername){
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

class userlistholder extends RecyclerView.ViewHolder{

    TextView useritemusername;
    ImageButton addfriendbtn;
    public userlistholder(@NonNull View itemView) {
        super(itemView);

        useritemusername = itemView.findViewById(R.id.useritemusername);
        addfriendbtn = itemView.findViewById(R.id.addfriendbtn);

    }
}
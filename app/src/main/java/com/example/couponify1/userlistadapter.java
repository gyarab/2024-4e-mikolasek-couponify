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

public class userlistadapter extends RecyclerView.Adapter<userlistholder> {
    private Context context;
    private List<User> userlist;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String curuserid, curusername;
    ValueEventListener valueEventListener;

    public userlistadapter(Context context, List<User> userlist) {
        this.context = context;
        this.userlist = userlist;
    }

    @NonNull
    @Override
    public userlistholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                    rqlist.add(curusername);
                    mDatabase.child("rq").child(friendusername).setValue(rqlist);
                } else {
                    List<String> rqlist = new ArrayList<>();
                    rqlist.add(curusername);
                    mDatabase.child("rq").child(friendusername).setValue(rqlist);
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
}

class userlistholder extends RecyclerView.ViewHolder{

    TextView useritemusername;
    ImageButton addfriendbtn;
    CardView user_item;
    public userlistholder(@NonNull View itemView) {
        super(itemView);

        useritemusername = itemView.findViewById(R.id.useritemusername);
        addfriendbtn = itemView.findViewById(R.id.addfriendbtn);
        user_item = itemView.findViewById(R.id.user_item);

    }
}
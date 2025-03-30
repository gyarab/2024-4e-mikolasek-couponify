package com.example.couponify1;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.util.List;
import java.util.Objects;

public class friendslistadapter extends RecyclerView.Adapter<friendslistholder> {
    private Context context;
    private List<String> friendslist;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String curuserid, curusername;

    public friendslistadapter(Context context, List<String> friendslist){
        this.context = context;
        this.friendslist = friendslist;
    }

    @NonNull
    @Override
    public friendslistholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        findCuruser();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendslist_listiem, parent, false);
        return new friendslistholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull friendslistholder holder, int position) {
        String friendname = friendslist.get(position);
        holder.friendsitemusername.setText(friendname);
        holder.friends_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, frienddetail.class);
                intent.putExtra("selectedfriend", friendname);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                context.startActivity(intent);
                ((MainActivity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendslist.size();
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
}

class friendslistholder extends RecyclerView.ViewHolder {
    CardView friends_item;
    TextView friendsitemusername;

    public friendslistholder(@NonNull View itemView) {
        super(itemView);
        friends_item = itemView.findViewById(R.id.friends_item);
        friendsitemusername = itemView.findViewById(R.id.friendsitemusername);
    }
}

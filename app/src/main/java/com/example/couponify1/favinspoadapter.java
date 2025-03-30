package com.example.couponify1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

import java.util.List;
import java.util.Objects;

public class favinspoadapter extends RecyclerView.Adapter<favinspoholder>{
    List<InspoCoupon> favinspolist;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String curuserid, curusername;
    FirebaseUser curfirebaseuser;
    User curuser;

    public favinspoadapter(Context context, List<InspoCoupon> favinspolist) {
        this.context = context;
        this.favinspolist = favinspolist;
    }

    @NonNull
    @Override
    public favinspoholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mDatabase = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        mAuth = FirebaseAuth.getInstance();
        curfirebaseuser = mAuth.getCurrentUser();
        curuserid = curfirebaseuser.getUid();

        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    User userr = itemSnapshot.getValue(User.class);
                    if (Objects.equals(userr.getId(), curuserid)) {
                        curusername = userr.getUsername();
                        curuser = userr;
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inspo_listitem, parent, false);
        return new favinspoholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull favinspoholder holder, int position) {
        InspoCoupon c = favinspolist.get(position);
        holder.inspotitle.setText(c.getTitle());
        holder.inspodesc.setText(c.getDesc().substring(0, Math.min(c.getDesc().length(), 10)) + "...");
        holder.favbtn.setImageResource(R.drawable.baseline_favorite_24);
        holder.favbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("users").child(curuserid).child("favoritedinspo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                            InspoCoupon i = itemsnapshot.getValue(InspoCoupon.class);
                            if (Objects.equals(i.title, c.getTitle())) {
                                itemsnapshot.getRef().removeValue();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return favinspolist.size();
    }
}
class favinspoholder extends RecyclerView.ViewHolder {
    TextView inspotitle, inspodesc;
    ImageButton favbtn;
    public favinspoholder(@NonNull View itemView) {
        super(itemView);
        inspotitle = itemView.findViewById(R.id.inspotitle);
        inspodesc = itemView.findViewById(R.id.inspodesc);
        favbtn = itemView.findViewById(R.id.favbtn);
    }
}
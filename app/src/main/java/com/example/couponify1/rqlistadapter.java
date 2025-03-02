package com.example.couponify1;

import android.content.Context;
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

public class rqlistadapter extends RecyclerView.Adapter<rqlistholder> {
    private Context context;
    private List<String> rqlist;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String curuserid, curusername;
    FirebaseUser curfirebaseuser;
    User friend, curuser;



    public rqlistadapter(Context context, List<String> rqlist) {
        this.context = context;
        this.rqlist = rqlist;
    }




    @NonNull
    @Override
    public rqlistholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pendingrq_listitem, parent, false);
        return new rqlistholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull rqlistholder holder, int position) {
        String item = rqlist.get(position);
        System.out.println(item);
        holder.rqusername.setText(item);
        holder.denyrqbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                denyrq();
            }
        });
        holder.approverqbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendusername = rqlist.get(holder.getAdapterPosition());
                addfriend(friendusername);
            }
        });
    }

    private void addfriend(String friendusername) {
        if (curuser.getFriends() == null || curuser.getFriends().isEmpty()) {
            ArrayList<String> friends = new ArrayList<>();
            friends.add(friendusername);
            mDatabase.child("users").child(curuserid).child("friends").setValue(friends);
        } else {
            ArrayList<String> friends = curuser.getFriends();
            friends.add(friendusername);
            mDatabase.child("users").child(curuserid).child("friends").setValue(friends);
        }
        Toast.makeText(context, "Friend added.",
                Toast.LENGTH_SHORT).show();
    }

    private void denyrq() {
    }

    @Override
    public int getItemCount() {
        return rqlist.size();
    }
}
class rqlistholder extends RecyclerView.ViewHolder{

    TextView rqusername;
    ImageButton approverqbtn;
    ImageButton denyrqbtn;
    CardView rq_item;
    public rqlistholder(@NonNull View itemview){
        super(itemview);

        rqusername = itemview.findViewById(R.id.rqusername);
        approverqbtn = itemview.findViewById(R.id.approverqbtn);
        denyrqbtn = itemview.findViewById(R.id.denyrqbtn);
        rq_item = itemview.findViewById(R.id.rq_item);
    }
}

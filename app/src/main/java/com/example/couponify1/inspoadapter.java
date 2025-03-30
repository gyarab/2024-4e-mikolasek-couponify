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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class inspoadapter extends RecyclerView.Adapter<inspoholder>{
    List<InspoCoupon> inspocouponlist;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String curuserid, curusername;
    FirebaseUser curfirebaseuser;
    User curuser;

    public inspoadapter(Context context, List<InspoCoupon> inspocouponlist) {
        this.context = context;
        this.inspocouponlist = inspocouponlist;
    }

    @NonNull
    @Override
    public inspoholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        return new inspoholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull inspoholder holder, int position) {
        InspoCoupon c = inspocouponlist.get(position);
        holder.inspotitle.setText(c.getTitle());
        //display only the first 15 letters of description as preview
        holder.inspodesc.setText(c.getDesc().substring(0, Math.min(c.getDesc().length(), 15)) + "...");
        holder.inspo_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InspoCouponDetail.class);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                intent.putExtra("coupontitle", c.getTitle());
                intent.putExtra("coupondesc", c.getDesc());
                context.startActivity(intent);
                ((InspirationTab)context).finish();
            }
        });
        holder.favbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("users").child(curuserid).child("favoritedinspo").push().setValue(c);
                Intent intent = new Intent(context, InspirationTab.class);
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                context.startActivity(intent);
                ((MainActivity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return inspocouponlist.size();
    }
}
class inspoholder extends RecyclerView.ViewHolder {
    TextView inspotitle, inspodesc;
    ImageButton favbtn;
    CardView inspo_item;
    public inspoholder(@NonNull View itemView) {
        super(itemView);
        inspo_item = itemView.findViewById(R.id.inspo_item);
        inspotitle = itemView.findViewById(R.id.inspotitle);
        inspodesc = itemView.findViewById(R.id.inspodesc);
        favbtn = itemView.findViewById(R.id.favbtn);
    }
}
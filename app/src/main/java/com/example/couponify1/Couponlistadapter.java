package com.example.couponify1;



import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Couponlistadapter extends RecyclerView.Adapter<Couponlistholder> {
    private Context context;
    private List<Coupon> couponList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String curuserid, curusername;
    FirebaseUser curfirebaseuser;
    User curuser;

    public Couponlistadapter(Context context, List<Coupon> couponList) {
        this.context = context;
        this.couponList = couponList;
    }

    @NonNull
    @Override
    public Couponlistholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_listitem, parent, false);
        return new Couponlistholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Couponlistholder holder, int position) {
        Coupon coupon = couponList.get(position);
        holder.coupontitle.setText(coupon.getTitle());
        holder.coupondate.setText(coupon.getCreatedon());
        holder.coupons_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, coupondetail.class);
                intent.putExtra("coupontitle", coupon.getTitle());
                intent.putExtra("coupondate", coupon.getCreatedon());
                intent.putExtra("coupondesc", coupon.getDesc());
                intent.putExtra("writtenby", coupon.getWrittenby());
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }
}

class Couponlistholder extends RecyclerView.ViewHolder {
    CardView coupons_item;
    TextView coupontitle, coupondate;
    public Couponlistholder(@NonNull View itemView) {
        super(itemView);
        coupons_item = itemView.findViewById(R.id.coupons_item);
        coupontitle = itemView.findViewById(R.id.coupontitle);
        coupondate = itemView.findViewById(R.id.coupondate);
    }
}

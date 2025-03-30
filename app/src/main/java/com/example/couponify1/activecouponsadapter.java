package com.example.couponify1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class activecouponsadapter extends RecyclerView.Adapter<ActiveCouponsholder> {
    private Context context;
    private List<Coupon> activecouponList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String curuserid, curusername;
    FirebaseUser curfirebaseuser;
    User curuser;
    Boolean writtentome;

    public activecouponsadapter(Context context, List<Coupon> activecouponList) {
        this.activecouponList = activecouponList;
        this.context = context;
    }

    @NonNull
    @Override
    public ActiveCouponsholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mDatabase = FirebaseDatabase.getInstance("https://couponify1-636d2-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        mAuth = FirebaseAuth.getInstance();
        curfirebaseuser = mAuth.getCurrentUser();
        curuserid = curfirebaseuser.getUid();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activecoupons_listitem, parent, false);
        return new ActiveCouponsholder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ActiveCouponsholder holder, int position) {
        Coupon coupon = activecouponList.get(position);
        System.out.println("type: " + coupon.getType());
        if (Objects.equals(coupon.getType(), "GameCoupon")) {
            holder.activecoupons_item.setCardBackgroundColor(Color.parseColor("#FA9292"));
        }
        holder.coupontitle.setText(coupon.getTitle());
        mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot itemSnapshot: task.getResult().getChildren()) {
                    User userr = itemSnapshot.getValue(User.class);
                    if (Objects.equals(userr.getId(), curuserid)) {
                        curusername = userr.getUsername();
                        curuser = userr;
                        if (Objects.equals(coupon.getWrittento(), curusername)) {
                            holder.couponfrom_to.setText("From: " + coupon.getWrittenby());
                        } else {
                            holder.couponfrom_to.setText("To: " + coupon.getWrittento());
                        }
                        break;
                    }
                }
            }
        });
        holder.activecoupons_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activecoupondetail.class);
                intent.putExtra("coupontitle", coupon.getTitle());
                intent.putExtra("coupondate", coupon.getCreatedon());
                intent.putExtra("coupondesc", coupon.getDesc());
                intent.putExtra("writtenby", coupon.getWrittenby());
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                context.startActivity(intent);
                ((ActiveCoupons)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return activecouponList.size();
    }
}

class ActiveCouponsholder extends RecyclerView.ViewHolder {
    CardView activecoupons_item;
    TextView coupontitle, couponfrom_to;
    public ActiveCouponsholder(@NonNull View itemView) {
        super(itemView);
        activecoupons_item = itemView.findViewById(R.id.activecoupons_item);
        coupontitle = itemView.findViewById(R.id.coupontitle);
        couponfrom_to = itemView.findViewById(R.id.couponfrom_to);
    }
}
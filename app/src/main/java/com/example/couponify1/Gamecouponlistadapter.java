package com.example.couponify1;

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

public class Gamecouponlistadapter extends RecyclerView.Adapter<Gamecouponlistholder>{
    private Context context;
    private List<GameCoupon> gamecouponList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String curuserid, curusername;
    FirebaseUser curfirebaseuser;
    User curuser;
    Boolean isactive;

    public Gamecouponlistadapter(Context context, List<GameCoupon> gamecouponList) {
        this.context = context;
        this.gamecouponList = gamecouponList;
    }

    @NonNull
    @Override
    public Gamecouponlistholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gamecoupon_listitem, parent, false);
        return new Gamecouponlistholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Gamecouponlistholder holder, int position) {
        GameCoupon coupon = gamecouponList.get(position);
        isactive = coupon.isactive;
        holder.gamecoupontitle.setText(coupon.getTitle());
        holder.gamecouponfrom_to.setText("Written by " + coupon.writtenby);
        holder.gamecoupons_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, gamecoupondetail.class);
                intent.putExtra("coupontitle", coupon.getTitle());
                intent.putExtra("coupondate", coupon.getCreatedon());
                intent.putExtra("coupondesc", coupon.getDesc());
                intent.putExtra("writtenby", coupon.getWrittenby());
                intent.putExtra("writtento", coupon.getWrittento());
                intent.putExtra("curusername", curusername);
                intent.putExtra("curuserid", curuserid);
                intent.putExtra("isactive", isactive);
                context.startActivity(intent);
                ((gamecoupons)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return gamecouponList.size();
    }
}
class Gamecouponlistholder extends RecyclerView.ViewHolder {
    CardView gamecoupons_item;
    TextView gamecoupontitle, gamecouponfrom_to;
    public Gamecouponlistholder(@NonNull View itemView) {
        super(itemView);
        gamecoupons_item = itemView.findViewById(R.id.gamecoupons_item);
        gamecoupontitle = itemView.findViewById(R.id.gamecoupontitle);
        gamecouponfrom_to = itemView.findViewById(R.id.gamecouponfrom_to);
    }
}
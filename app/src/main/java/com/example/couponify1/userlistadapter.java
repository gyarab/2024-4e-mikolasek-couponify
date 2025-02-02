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

import java.util.List;

public class userlistadapter extends RecyclerView.Adapter<userlistholder> {
    private Context context;
    private List<User> userlist;

    public userlistadapter(Context context, List<User> userlist) {
        this.context = context;
        this.userlist = userlist;
    }

    @NonNull
    @Override
    public userlistholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_listitem, parent, false);
        return new userlistholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userlistholder holder, int position) {
        holder.useritemusername.setText(userlist.get(position).getUsername());
        holder.addfriendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("adding friend");
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
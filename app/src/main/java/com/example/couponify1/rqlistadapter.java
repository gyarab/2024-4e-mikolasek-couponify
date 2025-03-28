package com.example.couponify1;

import android.content.Context;
import android.provider.ContactsContract;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class rqlistadapter extends RecyclerView.Adapter<rqlistholder> {
    private Context context;
    private List<String> rqlist, curuserfriends;
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
                        curuserfriends = userr.getFriends();
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
        //System.out.println(item);
        holder.rqusername.setText(item);
        holder.denyrqbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendusername = rqlist.get(holder.getAdapterPosition());
                removerq(friendusername);
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
        if (curuserfriends.isEmpty() || curuserfriends == null) {
            System.out.println("curusergetfriends is empty");
            List<String> friends = new ArrayList<>();
            friends.add(friendusername);
            mDatabase.child("users").child(curuserid).child("friends").setValue(friends);
        } else {
            curuserfriends.add(friendusername);
            mDatabase.child("users").child(curuserid).child("friends").setValue(curuserfriends);
        }

        mDatabase.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot itemsnapshot: task.getResult().getChildren()) {
                    User userr = itemsnapshot.getValue(User.class);
                    if (userr.getUsername().equals(friendusername)) {
                        String userrid = userr.getId();
                        sendNotifWithID("Friend request accepted!", curusername + " has added you as a friend.", userrid);
                        System.out.println(userrid);
                        mDatabase.child("users").child(userrid).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                List<String> userfriends = (List<String>) task.getResult().getValue();
                                if (userfriends == null || userfriends.isEmpty() ) {
                                    List<String> friendlist = new ArrayList<>();
                                    friendlist.add(curusername);
                                    mDatabase.child("users").child(userr.getId()).child("friends").setValue(friendlist);
                                } else {
                                    List<String> friendlist = userfriends;
                                    friendlist.add(curusername);
                                    mDatabase.child("users").child(userr.getId()).child("friends").setValue(friendlist);
                                }
                            }
                        });
                        break;
                    }
                }
            }
        });
        removerq(friendusername);
        Toast.makeText(context, "Friend added.",
                Toast.LENGTH_SHORT).show();
    }

    private void removerq(String friendusername) {
        mDatabase.child("rq").child(curusername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                List<String> list = snapshot.getValue(t);
                list.remove(friendusername);
                mDatabase.child("rq").child(curusername).setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return rqlist.size();
    }
    public void sendNotifWithID(String title, String desc, String friendid){
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

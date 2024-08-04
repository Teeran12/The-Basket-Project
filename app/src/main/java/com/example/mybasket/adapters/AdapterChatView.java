package com.example.mybasket.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybasket.R;
import com.example.mybasket.activities.ChatActivity;
import com.example.mybasket.models.ModelChatView;
import com.example.mybasket.models.ModelReview;
import com.example.mybasket.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterChatView extends RecyclerView.Adapter<AdapterChatView.MyHolder> {


    Context context;
    List<ModelUser> userList;
    private HashMap<String, String> lastMessageMap;

    public AdapterChatView(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String shopUid = userList.get(position).getUid();
        String myUid = userList.get(position).getUid();
        String name = userList.get(position).getName();
        String profileImage = userList.get(position).getProfileImage();
        String lastMessage = lastMessageMap.get(shopUid);


        holder.nameTv.setText(name);
        if (lastMessage==null || lastMessage.equals("default")){
            holder.lastMessageTv.setVisibility(View.GONE);
        }
        else {
            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lastMessage);
        }
        try{
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_image).into(holder.profileImageIv);

        }
        catch (Exception e){
            Picasso.get().load(R.drawable.ic_person_image).into(holder.profileImageIv);

        }
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //start chat activity with the user
               Intent intent = new Intent(context, ChatActivity.class);
               intent.putExtra("shopUid", shopUid);
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               context.startActivity(intent);
           }
       });
    }

    public void setLastMessageMap(String uid, String lastMessage){
        lastMessageMap.put(uid, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{

        private ImageView profileImageIv;
        private TextView nameTv,lastMessageTv, dateTv;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileImageIv = itemView.findViewById(R.id.profileImageIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
            dateTv = itemView.findViewById(R.id.dateTv);
        }
    }
}

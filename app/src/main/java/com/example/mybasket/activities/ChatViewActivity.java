package com.example.mybasket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mybasket.R;
import com.example.mybasket.adapters.AdapterChatView;
import com.example.mybasket.models.ModelChat;
import com.example.mybasket.models.ModelChatView;
import com.example.mybasket.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatViewActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private EditText searchChatRl;
    private RecyclerView chatsRv;

    private FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    private List<ModelChatView> chatlistList;
    List<ModelUser> userList;
    DatabaseReference reference;
    private AdapterChatView adapterChatView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        backBtn = findViewById(R.id.backBtn);
        searchChatRl = findViewById(R.id.searchChatRl);
        chatsRv = findViewById(R.id.chatsRv);

        chatlistList = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(currentUser.getUid());
        Query query = reference.orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatlistList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChatView chatlist = ds.getValue(ModelChatView.class);
                    chatlistList.add(chatlist);
                }
                loadChat();
                if (chatlistList.isEmpty()) {
                    Toast.makeText(ChatViewActivity.this, "Oops...No active chat...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadChat() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser user = ds.getValue(ModelUser.class);
                    for (ModelChatView chatlist: chatlistList){
                        if(user.getUid() != null && user.getUid().equals(chatlist.getUid())){
                            userList.add(user);
                            break;
                        }
                    }
                    //adapter
                    adapterChatView = new AdapterChatView(getApplicationContext(), userList);
                    //set adapter
                    chatsRv.setAdapter(adapterChatView);
                    //set last message
                    for (int i=0; i<userList.size(); i++){
                        lastMessage(userList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void lastMessage(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat==null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null ){
                        continue;
                    }
                    if (chat.getReceiver().equals(currentUser.getUid()) &&
                            chat.getSender().equals(uid) || chat.getReceiver().equals(uid)
                            && chat.getSender().equals(currentUser.getUid())){
                        //instead of displaying url in message show "sent image"
                            theLastMessage = chat.getMessage();


                    }
                }
                adapterChatView.setLastMessageMap(uid, theLastMessage);
                adapterChatView.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
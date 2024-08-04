package com.example.mybasket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybasket.R;
import com.example.mybasket.adapters.AdapterOrderCustomer;
import com.example.mybasket.adapters.AdapterShop;
import com.example.mybasket.models.ModelOrderCustomer;
import com.example.mybasket.models.ModelShops;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class customerMain extends AppCompatActivity {

    private ImageButton editProfileBtn, logoutBtn;
    private TextView nameTv, emailTv, phoneTv,tabShopsTv,tabOrderTv;
    private ImageView profilePicIv;
    private RelativeLayout shopsRl, orderRl;
    private RecyclerView shopsRv, ordersRv;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelShops> shopsList;
    private AdapterShop adapterShop;

    private ArrayList<ModelOrderCustomer> ordersList;
    private AdapterOrderCustomer adapterOrderCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        logoutBtn = findViewById(R.id.logoutBtn);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        tabShopsTv = findViewById(R.id.tabShopsTv);
        tabOrderTv = findViewById(R.id.tabOrderTv);
        shopsRl = findViewById(R.id.shopsRl);
        orderRl = findViewById(R.id.orderRl);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        profilePicIv = findViewById(R.id.profilePicIv);
        shopsRv = findViewById(R.id.shopsRv);
        ordersRv = findViewById(R.id.ordersRv);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        
        //at start show shops ui
        showShopsUI();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make offline
                //sign out
                //go to login activity
                makeMeOffline();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open edit profile user
                startActivity(new Intent(customerMain.this, EditProfileCustomer.class));

            }
        });

        tabShopsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show shops
                showShopsUI();
            }
        });
        tabOrderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show orders
                showOrdersUI();

            }
        });
    }

    private void showShopsUI() {
        //show shop ui, hide orders ui

        shopsRl.setVisibility(View.VISIBLE);
        orderRl.setVisibility(View.GONE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.black));
        tabShopsTv.setBackgroundResource(R.drawable.rec_04);

        tabOrderTv.setTextColor(getResources().getColor(R.color.white));
        tabOrderTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {
        //show order ui, hide shops ui

        shopsRl.setVisibility(View.GONE);
        orderRl.setVisibility(View.VISIBLE);

        tabOrderTv.setTextColor(getResources().getColor(R.color.black));
        tabOrderTv.setBackgroundResource(R.drawable.rec_04);

        tabShopsTv.setTextColor(getResources().getColor(R.color.white));
        tabShopsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void makeMeOffline() {
        //after logging in , make user offline
        progressDialog.setMessage("Logging Out");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //update successfully
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss();
                        Toast.makeText(customerMain.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(customerMain.this, LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }

    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            //get user data
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String city = ""+ds.child("city").getValue();

                            //set user data
                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person).into(profilePicIv);

                            }
                            catch (Exception e){
                                profilePicIv.setImageResource(R.drawable.ic_person);

                            }

                            // load only those shops that are in the city of user
                            loadShops(city);
                            loadOrders();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadOrders() {
        //init order list
        ordersList = new ArrayList<>();

        //get orders
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot ds: snapshot.getChildren()){
                                            ModelOrderCustomer modelOrderCustomer = ds.getValue(ModelOrderCustomer.class);

                                            //add to list
                                            ordersList.add(modelOrderCustomer);
                                        }
                                        //setup adapter
                                        adapterOrderCustomer = new AdapterOrderCustomer(customerMain.this, ordersList);
                                        //set to recyclerview
                                        ordersRv.setAdapter(adapterOrderCustomer);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadShops(String myCity) {
        //init list
        shopsList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("Admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding
                        shopsList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelShops modelShops = ds.getValue(ModelShops.class);

                            String shopCity = ""+ds.child("city").getValue();

                            //show only user city shops
                            if (shopCity.equals(myCity)){
                                shopsList.add(modelShops);
                            }

                            //if you want to display all shops, skip the if statement and add this
                            //shopsList.add(modelShops);


                        }
                        //setup adapter
                        adapterShop = new AdapterShop(customerMain.this, shopsList);
                        //set adapter to recyclerview
                        shopsRv.setAdapter(adapterShop);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
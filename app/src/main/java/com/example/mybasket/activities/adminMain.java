package com.example.mybasket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybasket.adapters.AdapterOrderShop;
import com.example.mybasket.adapters.AdapterProductAdmin;
import com.example.mybasket.Constants;
import com.example.mybasket.models.ModelOrderShop;
import com.example.mybasket.models.ModelProduct;
import com.example.mybasket.R;
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

public class adminMain extends AppCompatActivity {

    private ImageButton addProductBtn,editProfileBtn,logoutBtn,filterProductBtn, filterOrderBtn, reviewsBtn, chatBtn,reportBtn;
    private TextView nameTv, shopNameTv, emailTv,tabProductTv,tabOrderTv,filteredProductTv,filteredOrdersTv;
    private EditText searchProductRl;
    private ImageView shopPic;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private RelativeLayout productRl,orderRl;
    private RecyclerView productsRv, ordersRv;

    private ArrayList<ModelProduct> productList;
    private AdapterProductAdmin adapterProductAdmin;

    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        nameTv = findViewById(R.id.nameTv);
        shopNameTv = findViewById(R.id.shopNameTv);
        emailTv = findViewById(R.id.emailTv);
        tabProductTv = findViewById(R.id.tabProductTv);
        tabOrderTv = findViewById(R.id.tabOrderTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        addProductBtn = findViewById(R.id.addProductBtn);
        shopPic = findViewById(R.id.shopPic);
        productRl = findViewById(R.id.productRl);
        orderRl = findViewById(R.id.orderRl);
        searchProductRl = findViewById(R.id.searchProductRl);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        filteredProductTv = findViewById(R.id.filteredProductTv);
        productsRv = findViewById(R.id.productsRv);
        filteredOrdersTv = findViewById(R.id.filteredOrdersTv);
        filterOrderBtn = findViewById(R.id.filterOrderBtn);
        ordersRv = findViewById(R.id.ordersRv);
        reviewsBtn = findViewById(R.id.reviewsBtn);
        chatBtn = findViewById(R.id.chatBtn);
      //  reportBtn = findViewById(R.id.reportBtn);



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadAllProducts();
        loadAllOrders();

        showProductsUI();

//search
        searchProductRl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductAdmin.getFilter().filter(s);


                }
                catch (Exception e){
                    e.printStackTrace();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                //open edit profile admin
                startActivity(new Intent(adminMain.this, EditProfileAdmin.class));

            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open add product
                startActivity(new Intent(adminMain.this, Add_product.class));
            }
        });

        tabProductTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load products
                showProductsUI();

            }
        });
        tabOrderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load orders
                showOrdersUI();
            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(adminMain.this);
                builder.setTitle("Filter Products:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                                String selected = Constants.productCategories1[which];
                                filteredProductTv.setText(selected);
                                if (selected.equals("All")){
                                    loadAllProducts();
                                }
                                else{
                                    loadFilteredProducts(selected);
                                }

                            }
                        })
                        .show();
            }
        });

        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //option to display dialog
                String[] options = {"All", "In Progress", "Completed", "Cancelled"};
                //dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(adminMain.this);
                builder.setTitle("Filter Orders")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // handle item clicks
                                if (which==0){
                                    //all clicked
                                    filteredOrdersTv.setText("Showing All Orders");
                                    adapterOrderShop.getFilter().filter("");//show all orders
                                }
                                else{
                                    String optionClicked = options[which];
                                    filteredOrdersTv.setText("Showing "+optionClicked+" Orders");//e.g. Showing Completed Orders
                                    adapterOrderShop.getFilter().filter(optionClicked);
                                }

                            }
                        })
                        .show();
            }
        });

        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open same reviews activity as used in customer main page
                Intent intent = new Intent(adminMain.this, ShopReviewsActivity.class);
                intent.putExtra("shopUid", ""+firebaseAuth.getUid());
                startActivity(intent);
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open same reviews activity as used in customer main page
                Intent intent = new Intent(adminMain.this, ChatViewActivity.class);
                intent.putExtra("myUid", ""+firebaseAuth.getUid());
                startActivity(intent);
            }
        });

       // reportBtn.setOnClickListener(new View.OnClickListener() {
        //    @Override
         //   public void onClick(View v) {
         //       startActivity(new Intent(adminMain.this, MonthlyAnalytics.class));
        //    }
      //  });

    }

    private void loadAllOrders() {
        //init array list
        orderShopArrayList = new ArrayList<>();

        //load orders of shop
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clearlist before adding new data
                        orderShopArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderShop modelOrderShop = ds.getValue(ModelOrderShop.class);
                            //add to list
                            orderShopArrayList.add(modelOrderShop);
                        }
                        //setup adapter
                        adapterOrderShop = new AdapterOrderShop(adminMain.this, orderShopArrayList);
                        //set Adapter to recyclerview
                        ordersRv.setAdapter(adapterOrderShop);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadFilteredProducts(String selected) {
        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            String productCategory = ""+ds.child("productCategory").getValue();


                            if (selected.equals(productCategory)){
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }


                        }
                        adapterProductAdmin = new AdapterProductAdmin(adminMain.this, productList);

                        productsRv.setAdapter(adapterProductAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts() {
        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);

                        }
                        adapterProductAdmin = new AdapterProductAdmin(adminMain.this, productList);

                        productsRv.setAdapter(adapterProductAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showProductsUI() {
        //show product ui and hide orders ui
        productRl.setVisibility(View.VISIBLE);
        orderRl.setVisibility(View.GONE);

        tabProductTv.setTextColor(getResources().getColor(R.color.black));
        tabProductTv.setBackgroundResource(R.drawable.rec_04);

        tabOrderTv.setTextColor(getResources().getColor(R.color.white));
        tabOrderTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void showOrdersUI() {
        //show orders ui and hide product ui
        orderRl.setVisibility(View.VISIBLE);
        productRl.setVisibility(View.GONE);

        tabOrderTv.setTextColor(getResources().getColor(R.color.black));
        tabOrderTv.setBackgroundResource(R.drawable.rec_04);

        tabProductTv.setTextColor(getResources().getColor(R.color.white));
        tabProductTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
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
                        Toast.makeText(adminMain.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(adminMain.this, LoginActivity.class));
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
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            String accountType = "" + ds.child("accountType").getValue();
                            String email = "" + ds.child("email").getValue();
                            String retailName = "" + ds.child("retailName").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();

                            //set data to ui
                            nameTv.setText(name);
                            emailTv.setText(email);
                            shopNameTv.setText(retailName);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_black).into(shopPic);
                            } catch (Exception e) {
                                shopPic.setImageResource(R.drawable.ic_store_black);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}



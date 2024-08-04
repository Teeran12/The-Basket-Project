package com.example.mybasket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybasket.FilterOrderShop;
import com.example.mybasket.R;
import com.example.mybasket.activities.OrderDetailsAdminActivity;
import com.example.mybasket.activities.OrderDetailsCustomerActivity;
import com.example.mybasket.models.ModelOrderShop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderShop extends RecyclerView.Adapter<AdapterOrderShop.HolderOrderShop> implements Filterable {

    private Context context;
    public ArrayList<ModelOrderShop> orderShopArrayList, filterList;
    private FilterOrderShop filter;

    public AdapterOrderShop(Context context, ArrayList<ModelOrderShop> orderShopArrayList) {
        this.context = context;
        this.orderShopArrayList = orderShopArrayList;
        this.filterList = orderShopArrayList;
    }

    @NonNull
    @Override
    public HolderOrderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_admin, parent, false);
        return new HolderOrderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderShop holder, int position) {
        //get data at position
        ModelOrderShop modelOrderShop = orderShopArrayList.get(position);
        String orderId = modelOrderShop.getOrderId();
        String orderBy = modelOrderShop.getOrderBy();
        String orderCost = modelOrderShop.getOrderCost();
        String orderStatus = modelOrderShop.getOrderStatus();
        String orderTime = modelOrderShop.getOrderTime();
        String orderTo = modelOrderShop.getOrderTo();

        //load user info
        loadUserInfo(modelOrderShop, holder);

        //set data
        holder.amountTv.setText("Amount: $" +orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("Order ID: " +orderId);
        //change order status text color
        if (orderStatus.equals("In Progress")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.primaryDark));
        } else if (orderStatus.equals("Completed")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.green));
        } else if (orderStatus.equals("Cancelled")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        //covert timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.getDateInstance().format(calendar.getTime());

        holder.orderDateTv.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open order details, we need to keys there, orderId, orderTo
                Intent intent = new Intent(context, OrderDetailsAdminActivity.class);
                intent.putExtra("orderId", orderId);//to load order info
                intent.putExtra("orderBy", orderBy);//to load info of the customer who placed order
                context.startActivity(intent);

            }
        });

    }

    private void loadUserInfo(ModelOrderShop modelOrderShop, HolderOrderShop holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderShop.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = ""+snapshot.child("email").getValue();
                        holder.emailTv.setText(email);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderShopArrayList.size();//return size of list/ number of records

    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            //init filter
            filter = new FilterOrderShop(this, filterList);
        }
        return filter;
    }

    //view holder class for row_order_admin.xml

    class HolderOrderShop extends RecyclerView.ViewHolder{

        //ui views of row_order_admin.xml
        private TextView orderIdTv, orderDateTv, emailTv, amountTv, statusTv;


        public HolderOrderShop(@NonNull View itemView) {
            super(itemView);

            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
        }
    }
}

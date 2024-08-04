package com.example.mybasket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybasket.R;
import com.example.mybasket.activities.OrderDetailsCustomerActivity;
import com.example.mybasket.models.ModelOrderCustomer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class AdapterOrderCustomer extends RecyclerView.Adapter<AdapterOrderCustomer.HolderOrderCustomer> {

    private Context context;
    private ArrayList<ModelOrderCustomer> orderCustomerList;

    public AdapterOrderCustomer(Context context, ArrayList<ModelOrderCustomer> orderCustomerList) {
        this.context = context;
        this.orderCustomerList = orderCustomerList;
    }

    @NonNull
    @Override
    public HolderOrderCustomer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_customer, parent, false);
        return new HolderOrderCustomer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderCustomer holder, int position) {
        //get data
        ModelOrderCustomer modelOrderCustomer = orderCustomerList.get(position);
        String orderId = modelOrderCustomer.getOrderId();
        String orderBy = modelOrderCustomer.getOrderBy();
        String orderCost = modelOrderCustomer.getOrderCost();
        String orderStatus = modelOrderCustomer.getOrderStatus();
        String orderTime = modelOrderCustomer.getOrderTime();
        String orderTo = modelOrderCustomer.getOrderTo();

        //get shop info
        loadShopInfo(modelOrderCustomer, holder);

        //set data
        holder.amountTv.setText("Amount: $" + orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("OrderID:" + orderId);
        //change order status text color
        if (orderStatus.equals("In Progress")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.black));
        } else if (orderStatus.equals("Completed")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.green));
        } else if (orderStatus.equals("Cancelled")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorRed));
        }
        //covert timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.getDateInstance().format(calendar.getTime());

        holder.dateTv.setText(formatedDate);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open order details, we need to keys there, orderId, orderTo
                Intent intent = new Intent(context, OrderDetailsCustomerActivity.class);
                intent.putExtra("orderTo", orderTo);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);

            }
        });


    }

    private void loadShopInfo(ModelOrderCustomer modelOrderCustomer, HolderOrderCustomer holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderCustomer.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopName = ""+snapshot.child("retailName").getValue();
                        holder.shopNameTv.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderCustomerList.size();
    }

    //view holder class
    class HolderOrderCustomer extends RecyclerView.ViewHolder{

        //views of layout
        private TextView orderIdTv, dateTv, shopNameTv, amountTv, statusTv;

        public HolderOrderCustomer(@NonNull View itemView) {
            super(itemView);

            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
        }
    }

}

package com.example.mybasket.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybasket.FilterProduct;
import com.example.mybasket.models.ModelProduct;
import com.example.mybasket.R;
import com.example.mybasket.activities.editProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductAdmin extends RecyclerView.Adapter<AdapterProductAdmin.HolderProductAdmin> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProduct filter;

    public AdapterProductAdmin(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout

        View view = LayoutInflater.from(context).inflate(R.layout.row_product_admin, parent, false);
        return new HolderProductAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductAdmin holder, int position) {
        //get data
        ModelProduct modelProduct = productList.get(position);
        String id = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String icon = modelProduct.getProductPic();
        String quantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();

        //set data

        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity);
        holder.DiscountNoteTv.setText(discountNote);
        holder.DiscountPriceTv.setText("$"+discountPrice);
        holder.originalPriceTv.setText("$"+originalPrice);

        if (discountAvailable.equals("true")){
            //product is on discount
            holder.DiscountPriceTv.setVisibility(View.VISIBLE);
            holder.DiscountNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else{
            //product is not on discount
            holder.DiscountPriceTv.setVisibility(View.GONE);
            holder.DiscountNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_store_black).into(holder.productPic);

        }
        catch (Exception e){
            holder.productPic.setImageResource(R.drawable.ic_cart_black);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle item clicks, show item details(in bottom sheet)
                detailsBottomSheet(modelProduct);
            }
        });


    }

    private void detailsBottomSheet(ModelProduct modelProduct) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflate view for bottomsheet
        View view = LayoutInflater.from(context).inflate(R.layout.activity_product_details, null);
        //set view to bottomsheet
        bottomSheetDialog.setContentView(view);

        //init views of bottomsheet
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
        ImageButton editBtn = view.findViewById(R.id.editBtn);
        ImageView productPic = view.findViewById(R.id.productPic);
        TextView DiscountNoteEt = view.findViewById(R.id.DiscountNoteEt);
        TextView titleEt = view.findViewById(R.id.titleEt);
        TextView descriptionEt = view.findViewById(R.id.descriptionEt);
        TextView categoryEt = view.findViewById(R.id.categoryEt);
        TextView quantityEt = view.findViewById(R.id.quantityEt);
        TextView DiscountPriceEt = view.findViewById(R.id.DiscountPriceEt);
        TextView originalPriceEt = view.findViewById(R.id.originalPriceEt);


        //get data
        String id = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String icon = modelProduct.getProductPic();
        String quantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();

        //set data

        titleEt.setText(title);
        descriptionEt.setText(productDescription);
        categoryEt.setText(productCategory);
        quantityEt.setText(quantity);
        DiscountNoteEt.setText(discountNote);
        DiscountPriceEt.setText("$"+ discountPrice);
        originalPriceEt.setText("$"+ originalPrice);

        if (discountAvailable.equals("true")){
            //product is on discount
            DiscountPriceEt.setVisibility(View.VISIBLE);
            DiscountNoteEt.setVisibility(View.VISIBLE);
            originalPriceEt.setPaintFlags(originalPriceEt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else{
            //product is not on discount
            DiscountPriceEt.setVisibility(View.GONE);
            DiscountNoteEt.setVisibility(View.GONE);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_store_black).into(productPic);

        }
        catch (Exception e){
            productPic.setImageResource(R.drawable.ic_cart_black);

        }

        //show dialog
        bottomSheetDialog.show();

        //edit clicks
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //open edit product activity, pass id of product
                Intent intent = new Intent(context, editProduct.class);
                intent.putExtra("productId", id);
                context.startActivity(intent);

            }
        });
//delete clicks
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //show delete confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete the product "+title+" ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteProduct(id);//id is product id

                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cancel, dismiss dialog
                                dialog.dismiss();

                            }
                        })
                        .show();

            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss bottom sheet
                bottomSheetDialog.dismiss();

            }
        });


    }

    private void deleteProduct(String id) {
        //delete product using its id

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //product deleted
                        Toast.makeText(context, "Product deleted...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Failed deleting product
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterProduct(this, filterList);
        }
        return filter;
    }

    class HolderProductAdmin extends RecyclerView.ViewHolder {
        //holds view of recycle views
        private ImageView productPic;
        private TextView DiscountNoteTv, titleTv,quantityTv,DiscountPriceTv,originalPriceTv;

        public HolderProductAdmin(@NonNull View itemView) {
            super(itemView);

            productPic = itemView.findViewById(R.id.productPic);
            DiscountNoteTv = itemView.findViewById(R.id.DiscountNoteTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            DiscountPriceTv = itemView.findViewById(R.id.DiscountPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);





        }
    }
}

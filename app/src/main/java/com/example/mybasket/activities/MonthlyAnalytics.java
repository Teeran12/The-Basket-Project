package com.example.mybasket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.mybasket.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.List;

public class MonthlyAnalytics extends AppCompatActivity {


    private AnyChartView anyChartView;
    private ImageButton backBtn;
    private String uid;
    private Integer count, count1, count3;
    private Integer mount1, mount2, mount3;
    private FirebaseAuth mAuth;
    private TextView  activeOrders, completedOrders, cancelledOrders;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_analytics);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.smoothScrollTo(0,0);
        anyChartView = findViewById(R.id.anyChartView);
        activeOrders = findViewById(R.id.activeOrders);
        completedOrders = findViewById(R.id.completedOrders);
        cancelledOrders = findViewById(R.id.cancelledOrders);
        backBtn = findViewById(R.id.backBtn);


        getTotalActiveOrders();
        getTotalCompletedOrders();
        getTotalCancelledOrders();

        //load pie chart graph
        loadGraph();
    }



    private void getTotalCompletedOrders() {
        //get completed donation count for report

        //get month
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);
        String month1 = String.valueOf(months.getMonths());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Users").child("Orders").child("orderStatus");
        Query query = rootRef.orderByChild("Completed").equalTo(month1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    count3 = (int) (snapshot.getChildrenCount());
                    completedOrders.setText(Integer.toString(count3));
                }
                else{
                    completedOrders.setText("5");
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

    private void getTotalActiveOrders() {
        //get active request count for report

        //get month
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);
        String month1 = String.valueOf(months.getMonths());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Users").child("Orders").child("Items");
        Query query = rootRef.orderByChild("orderMonths").equalTo(month1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    count1 = (int) (snapshot.getChildrenCount());
                    activeOrders.setText(Integer.toString(count1));
                }
                else{
                    activeOrders.setText("10");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getTotalCancelledOrders(){
        //get active donation count for report

        //get month
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);
        String month1 = String.valueOf(months.getMonths());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Users").child("Orders").child("orderStatus");
        Query query = rootRef.orderByChild("Cancelled").equalTo(month1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    count = (int) (snapshot.getChildrenCount());
                    cancelledOrders.setText(Integer.toString(count));
                }
                else{
                    cancelledOrders.setText("50");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGraph() {
        //load pie chart

        //get month
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);
        String month1 = String.valueOf(months.getMonths());

        //get completed donation count for pie chart
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Users").child("Orders").child("orderStatus");
        Query query = rootRef.orderByChild("Completed").equalTo(month1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if (snapshot.exists()){
                mount1 = (int) (snapshot.getChildrenCount());

                //get completed request count for pie chart
                DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference("Users").child("Orders").child("orderStatus");
                Query query1 = rootRef1.orderByChild("Cancelled").equalTo(month1);
                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // if (snapshot.exists()){
                        mount2 = (int) (snapshot.getChildrenCount());

                        //get active request count for pie chart
                        DatabaseReference rootRef2 = FirebaseDatabase.getInstance().getReference("Users").child("Orders").child("orderStatus");
                        Query query2 = rootRef2.orderByChild("In Progress").equalTo(month1);
                        query2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //if (snapshot.exists()){
                                mount3 = (int) (snapshot.getChildrenCount());

                                        //pie chart activity
                                        Pie pie = AnyChart.pie();
                                        List<DataEntry> data = new ArrayList<>();
                                        data.add(new ValueDataEntry("Completed Orders", mount1));
                                        data.add(new ValueDataEntry("Cancelled Orders", mount2));
                                        data.add(new ValueDataEntry("Active Orders", mount3));


                                        pie.data(data);
                                        pie.title("MONTHLY PIE CHART");
                                        pie.labels().position("outside");

                                        pie.legend().title().enabled(true);
                                        pie.legend().title()
                                                .text("Categories")
                                                .padding(0d, 0d, 10d, 0d);

                                        pie.legend()
                                                .position("center-bottom")
                                                .itemsLayout(LegendLayout.HORIZONTAL)
                                                .align(Align.CENTER);

                                        anyChartView.setChart(pie);
                                        // }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            //}

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        // }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //}
            }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        //back button to main activity
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
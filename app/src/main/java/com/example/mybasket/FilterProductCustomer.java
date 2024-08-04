package com.example.mybasket;

import android.widget.Filter;

import com.example.mybasket.adapters.AdapterProductAdmin;
import com.example.mybasket.adapters.AdapterProductCustomer;
import com.example.mybasket.models.ModelProduct;

import java.util.ArrayList;

public class FilterProductCustomer extends Filter {

    private AdapterProductCustomer adapter;

    private ArrayList<ModelProduct> filterList;

    public FilterProductCustomer(AdapterProductCustomer adapter, ArrayList<ModelProduct> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate data
        if (constraint != null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelProduct> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                if (filterList.get(i).getProductTitle().toUpperCase().contains(constraint) ||
                        filterList.get(i).getProductCategory().toUpperCase().contains(constraint) ){

                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.productsList = (ArrayList<ModelProduct>) results.values;

        adapter.notifyDataSetChanged();

    }
}

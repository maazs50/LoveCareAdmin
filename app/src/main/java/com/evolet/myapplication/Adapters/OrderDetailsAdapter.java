package com.evolet.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.evolet.myapplication.Items.OrderItems;
import com.evolet.myapplication.R;

import java.util.ArrayList;

public class OrderDetailsAdapter extends ArrayAdapter<OrderItems> {

    Context context;
    ArrayList<OrderItems> orderDetailsList;
    public OrderDetailsAdapter( Context context, ArrayList<OrderItems> orderDetailsList) {
        super(context,0,orderDetailsList);
        this.context=context;
        this.orderDetailsList=orderDetailsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView= LayoutInflater.from(context).inflate(R.layout.order_details_list_items,null);
        TextView item_name = (TextView)rootView.findViewById(R.id.item_name);
        TextView item_qty = (TextView)rootView.findViewById(R.id.item_quantity);
        String name=orderDetailsList.get(position).getItemName();
        String qty=orderDetailsList.get(position).getQuantity();
        item_name.setText(name);
        item_qty.setText(qty);


        return rootView;
    }
}
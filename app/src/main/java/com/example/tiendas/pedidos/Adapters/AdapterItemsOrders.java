package com.example.tiendas.pedidos.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tiendas.R;
import com.example.tiendas.Utils.Models.OrderDTO;

public class AdapterItemsOrders extends BaseAdapter {
    Context context;
    OrderDTO[] orders;
    public AdapterItemsOrders(Context context, OrderDTO[] orders){
        this.context = context;
        this.orders=orders;
    }

    @Override
    public int getCount() {
        return this.orders.length;
    }

    @Override
    public Object getItem(int position) {
        return this.orders[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View view = layoutInflater.inflate(R.layout.item_list_pedidos, null);
        TextView id=view.findViewById(R.id.orderId);
        TextView date = view.findViewById(R.id.dateOrder);
        TextView urgent = view.findViewById(R.id.urgentOrder);
        id.setText(String.valueOf(this.orders[position].getOrderId()));

        String dtStart = this.orders[position].getDate().split("T")[0];
        date.setText(dtStart);
        if(this.orders[position].isUrgent()){
            urgent.setText("Urgente");
            urgent.setTextColor(Color.RED);
        }else{
            urgent.setText("No urgente");
            urgent.setTextColor(Color.parseColor("#e1bc5c"));
        }
        return view;
    }
}

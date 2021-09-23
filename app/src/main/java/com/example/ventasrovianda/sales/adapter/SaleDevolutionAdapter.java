package com.example.ventasrovianda.sales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.bd.entities.Sale;
import com.example.ventasrovianda.sales.view.SaleViewContract;
import com.example.ventasrovianda.sales.view.SalesView;

import java.util.List;
import java.util.Map;

public class SaleDevolutionAdapter extends BaseAdapter {

    SaleViewContract mainV;
    Context context;
    List<Sale> sales;
    Map<String,String> mapDevolutionStatus;
    public SaleDevolutionAdapter(List<Sale> sales, Context context, SalesView mainV, Map<String,String> mapDevolutionStatus){
        this.mainV=mainV;
        this.context=context;
        this.sales=sales;
        this.mapDevolutionStatus = mapDevolutionStatus;
    }

    @Override
    public int getCount() {
        return this.sales.size();
    }

    @Override
    public Object getItem(int position) {
        return this.sales.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.sales.get(position).saleId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View view = layoutInflater.inflate(R.layout.list_sale_devolution_item,null);
        TextView client = view.findViewById(R.id.clientText);
        TextView folio = view.findViewById(R.id.folioText);
        TextView amount = view.findViewById(R.id.amountText);
        TextView status = view.findViewById(R.id.statusText);
        Button detailsButton = view.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainV.goToDevolutionSaleToView(sales.get(position).folio);
            }
        });
        client.setText(this.sales.get(position).clientName);
        folio.setText(this.sales.get(position).folio);
        amount.setText(this.sales.get(position).amount.toString());
        String statusStr =mapDevolutionStatus.get(this.sales.get(position).folio);
        if(statusStr.equals("PENDING")) {
            status.setText("Pendiente");
        }else if(statusStr.equals("DECLINED")){
            status.setText("Rechazada");
        }else if(statusStr.equals("ACCEPTED")){
            status.setText("Aceptada");
        }
        return view;
    }
}

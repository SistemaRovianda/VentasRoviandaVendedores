package com.example.ventasrovianda.sales.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.Models.SaleResponseDTO;
import com.example.ventasrovianda.sales.presenter.SalePresenterContract;
import com.example.ventasrovianda.sales.view.SaleViewContract;
import com.example.ventasrovianda.sales.view.SalesView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SaleListAdapter extends BaseAdapter {

    Context context;
    SaleResponseDTO[] sales;
    SalePresenterContract presenter;
    Boolean modeOffline;
    SaleViewContract viewM;
    public SaleListAdapter(Context context, SaleResponseDTO[] sales, SalePresenterContract presenter, Boolean modeOffline, SalesView viewM){
        this.context =context;
        this.sales =sales;
        this.presenter= presenter;
        this.modeOffline = modeOffline;
        this.viewM=viewM;
    }

    @Override
    public int getCount() {
        return this.sales.length;
    }

    @Override
    public Object getItem(int position) {
        return this.sales[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    boolean isLoading=false;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View view= layoutInflater.inflate(R.layout.sale_item_list, null);
        TextView folio = view.findViewById(R.id.foliosaleTextView);
        TextView clientName = view.findViewById(R.id.clientSaleTextView);
        TextView amount = view.findViewById(R.id.amountSaletextView);
        TextView status = view.findViewById(R.id.statusSaleTextView);
        MaterialButton optionsButton = view.findViewById(R.id.optionsButton);

        folio.setText("Folio: "+this.sales[position].getFolio());
        clientName.setText("Cliente: "+this.sales[position].getClient().getName());
        amount.setText("$"+this.sales[position].getAmount());
        String statusStr="";
        System.out.println("Status:"+this.sales[position].getStatusStr());
        System.out.println("DevolutionId: "+this.sales[position].getDevolutionid());
        System.out.println("Status autorized: "+this.sales[position].getCancelAutorized());
        if(this.sales[position].getDevolutionid()==null) {
            if (this.sales[position].getStatusStr().equals("CANCELED")) {
                if (this.sales[position].getCancelAutorized() != null && this.sales[position].getCancelAutorized().equals("true")) {
                    statusStr = "CANCELADO";
                } else {
                    System.out.println("Status de saleList: " + this.sales[position].getCancelAutorized());
                    statusStr = "CANCEL. PEND.";
                }
            } else {
                System.out.println("Status: " + this.sales[position].isStatus());
                if ((this.sales[position].getTypeSale().equals("CREDITO") || this.sales[position].getTypeSale().equals("Crédito")) && this.sales[position].isStatus() == true) {
                    statusStr = "ADEUDO";
                } else {
                    statusStr = "ACTIVO";
                }
            }
            status.setText("Estatus: "+statusStr);
        }else{
            statusStr=this.sales[position].getDevolutionStatus();
            if(statusStr.equals("PENDING")) {
                status.setText("DEVOLUCIÓN: PENDIENTE");
            }else if(statusStr.equals("DECLINED")){
                status.setText("DEVOLUCIÓN: RECHAZADO");
            }else if(statusStr.equals("ACCEPTED")){
                status.setText("DEVOLUCIÓN: ACEPTADA");
            }
        }


        status.setTextColor(Color.GREEN);

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        viewM.showOptionsSale(sales[position]);

            }
        });

        return view;
    }

}

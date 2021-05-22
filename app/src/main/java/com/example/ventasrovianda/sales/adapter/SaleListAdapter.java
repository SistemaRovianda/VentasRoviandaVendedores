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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SaleListAdapter extends BaseAdapter {

    Context context;
    SaleResponseDTO[] sales;
    SalePresenterContract presenter;
    Boolean modeOffline;
    public SaleListAdapter(Context context, SaleResponseDTO[] sales, SalePresenterContract presenter,Boolean modeOffline){
        this.context =context;
        this.sales =sales;
        this.presenter= presenter;
        this.modeOffline = modeOffline;
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
        MaterialButton cancelSale = view.findViewById(R.id.cancelSaleButton);
        MaterialButton reprint = view.findViewById(R.id.reprintTickerButton);
        reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!modeOffline) {
                    presenter.verifyConnectionPrinterToGetTicket(sales[position].getSaleId());
                }else{
                    presenter.verifyConnectionPrinterToGetTicketOffline(sales[position].getFolio());
                }
            }
        });
        folio.setText("Folio: "+this.sales[position].getFolio());
        clientName.setText("Cliente: "+this.sales[position].getClient().getName());
        amount.setText("$"+this.sales[position].getAmount());
        String statusStr="";
        System.out.println("Status:"+this.sales[position].getStatusStr());
        if(this.sales[position].getStatusStr().equals("CANCELED")){
            statusStr ="CANCELADO";
            cancelSale.setVisibility(View.INVISIBLE);
            cancelSale.setEnabled(false);
        }else{
            System.out.println("Status: "+this.sales[position].isStatus());
            if((this.sales[position].getTypeSale().equals("CREDITO") || this.sales[position].getTypeSale().equals("Crédito")) && this.sales[position].isStatus()==true){
                statusStr="ADEUDO";
            }else {
                statusStr = "ACTIVO";
            }
        }

        status.setText("Estatus: "+statusStr);
        status.setTextColor(Color.GREEN);
        cancelSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoading==false) {
                    isLoading = true;
                    genericMessage(sales[position].getSaleId(),sales[position].getFolio());
                }
            }
        });


        return view;
    }
    public void genericMessage(Long saleId,String folio){

        AlertDialog dialog = new MaterialAlertDialogBuilder(this.context).setTitle("Cancelación de venta")
                .setMessage("¿Está seguro que desea cancelar la venta?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!modeOffline) {
                            presenter.cancelSale(saleId);
                        }else{
                            presenter.cancelSaleOffline(folio);
                        }
                    }
                }).setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        isLoading=false;
                    }
                }).create();
        dialog.show();
    }
}

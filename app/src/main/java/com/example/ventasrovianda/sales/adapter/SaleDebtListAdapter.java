package com.example.ventasrovianda.sales.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.Models.SaleDTO;
import com.example.ventasrovianda.Utils.Models.SaleResponseDTO;
import com.example.ventasrovianda.sales.presenter.SalePresenterContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

public class SaleDebtListAdapter extends  BaseAdapter {




        Context context;
        SaleResponseDTO[] sales;
        SalePresenterContract presenter;
        public SaleDebtListAdapter(Context context, SaleResponseDTO[] sales, SalePresenterContract presenter){
            this.context =context;
            this.sales =sales;
            this.presenter= presenter;
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
            View view= layoutInflater.inflate(R.layout.sale_item_list_debt, null);
            TextView folio = view.findViewById(R.id.foliosaleTextView);
            TextView clientName = view.findViewById(R.id.clientSaleTextView);
            TextView amount = view.findViewById(R.id.amountSaletextView);
            TextView status = view.findViewById(R.id.statusSaleTextView);
            MaterialButton pay = view.findViewById(R.id.payDebt);
            MaterialButton reprintPay = view.findViewById(R.id.reprintPayDeb);
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.checkPayDeb(sales[position]);
                }
            });
            reprintPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.reprintPaydeb(sales[position]);
                }
            });
            folio.setText("Folio: "+this.sales[position].getFolio());
            clientName.setText("Cliente: "+this.sales[position].getClient().getName());
            amount.setText("$"+this.sales[position].getAmount());
            String statusStr = "";
            if(this.sales[position].isStatus()) {
                pay.setVisibility(View.VISIBLE);
                pay.setEnabled(true);
                reprintPay.setVisibility(View.GONE);
                reprintPay.setEnabled(false);
                statusStr = "POR COBRAR";
            }else{
                pay.setVisibility(View.GONE);
                pay.setEnabled(false);
                reprintPay.setVisibility(View.VISIBLE);
                reprintPay.setEnabled(true);
                statusStr = "COBRADO";
            }
            status.setText("Estatus: " + statusStr);

            return view;
        }



}

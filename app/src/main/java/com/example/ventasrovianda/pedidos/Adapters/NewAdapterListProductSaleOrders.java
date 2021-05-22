package com.example.ventasrovianda.pedidos.Adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.Models.ProductRoviandaToSale;
import com.example.ventasrovianda.pedidos.view.PedidoForm;
import com.example.ventasrovianda.pedidos.view.PedidoFormContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class NewAdapterListProductSaleOrders extends BaseAdapter {

        Context context;
        ProductRoviandaToSale products[];

        PedidoFormContract pedidoFormContract;

        public NewAdapterListProductSaleOrders(Context appContext, ProductRoviandaToSale[] products, PedidoForm pedidoForm){
            this.context=appContext;
            this.products=products;
            this.pedidoFormContract =pedidoForm;
        }

        @Override
        public int getCount() {
            return this.products.length;
        }

        @Override
        public Object getItem(int position) {
            return this.products[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view  = convertView;
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            view= layoutInflater.inflate(R.layout.item_list_product_sale_orders, null);

            /*MaterialButton button = view.findViewById(R.id.observations);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(context);
                    builder.setTitle(products[position].getNameProduct()+"\n"+products[position].getPresentationType());
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    View view = layoutInflater.inflate(R.layout.modalobservations, null);
                    TextInputLayout observations = view.findViewById(R.id.observationsOrder);
                    observations.getEditText().setText(products[position].getObservations());
                    builder.setView(view);
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            products[position].setObservations(observations.getEditText().getText().toString());
                        }
                    });
                    builder.show();
                    
                }
            });*/

            TextView code = (TextView) view.findViewById(R.id.productCode);
            code.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    pedidoFormContract.removeItemOfList(position);
                    return false;
                }
            });
            TextView name = (TextView) view.findViewById(R.id.productName);
            TextView weight = (TextView) view.findViewById(R.id.productWeight);
            code.setText(products[position].getKeySae());
            name.setText(products[position].getNameProduct()+"\n"+products[position].getPresentationType());
            name.setTextSize(14);
            weight.setText(String.valueOf(products[position].getWeight()));
            //image.setImageResource("");
            return  view;
        }

    }



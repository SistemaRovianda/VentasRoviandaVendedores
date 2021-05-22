package com.example.ventasrovianda.home.adapters;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class AdapterListProductSale extends BaseAdapter {

    Context context;
    ProductRoviandaToSale products[];

    public AdapterListProductSale(Context appContext, ProductRoviandaToSale[] products){
        this.context=appContext;
        this.products=products;
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
        view= layoutInflater.inflate(R.layout.item_list_product_sale, null);

        TextView code = (TextView) view.findViewById(R.id.productCode);
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

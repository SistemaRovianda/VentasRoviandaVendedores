package com.example.ventasrovianda.devolutions.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.bd.entities.SubSale;
import com.example.ventasrovianda.devolutions.view.DevolutionsView;
import com.example.ventasrovianda.devolutions.view.DevolutionsViewContract;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

public class ItemListDevolutionAdapter extends BaseAdapter {

    List<SubSale> items;
    Context context;
    DevolutionsViewContract viewM;
    Map<Integer,Float> mapSubSales;
    TextView devolutionTotalTicket;
    String type;
    public ItemListDevolutionAdapter(List<SubSale> subSales, Context context, DevolutionsView devolutionsView, Map<Integer,Float> mapSubSales,TextView devolutionTotalTicket,String type){
        this.items =subSales;
        this.context=context;
        this.viewM=devolutionsView;
        this.mapSubSales = mapSubSales;
        this.devolutionTotalTicket=devolutionTotalTicket;
        this.type=type;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.items.get(position).subSaleId;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View view = layoutInflater.inflate(R.layout.item_devolution,null);
        SubSale subSale = this.items.get(position);
        TextInputLayout originalQuantity,modifiedQuantity;
        TextView originalAmount,modifiedAmount,nameProduct;
        originalQuantity = view.findViewById(R.id.devolutionItemOriginalQuantity);
        modifiedQuantity= view.findViewById(R.id.devolutionItemModifiedQuantity);
        nameProduct = view.findViewById(R.id.devolutionItemNameProduct);
        originalAmount= view.findViewById(R.id.devolutionItemOriginalPrice);
        modifiedAmount = view.findViewById(R.id.devolutionItemModifiedPrice);
        nameProduct.setText(subSale.productName+" "+subSale.productPresentationType);
        originalQuantity.getEditText().setText(subSale.quantity.toString());
        if(this.type.equals("EDIT")){
            modifiedQuantity.setEnabled(false);
        }
        modifiedQuantity.getEditText().setText(mapSubSales.get(subSale.subSaleId).toString());
        originalAmount.setText("Total: $"+subSale.price);
        modifiedAmount.setText("Total: $"+(subSale.price/subSale.quantity)*mapSubSales.get(subSale.subSaleId));
        Selection.setSelection(modifiedQuantity.getEditText().getEditableText(),modifiedQuantity.getEditText().getText().toString().length());
        modifiedQuantity.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().endsWith(".") && !s.toString().isEmpty()) {
                    String quantityModified = modifiedQuantity.getEditText().getText().toString();
                    if (quantityModified == null || quantityModified.isEmpty()) {
                        quantityModified = "0";
                    }
                    Float amount = (subSale.price / subSale.quantity) * (Float.parseFloat(quantityModified));
                    modifiedAmount.setText("Total: $" + amount);
                    mapSubSales.put(subSale.subSaleId,Float.parseFloat(quantityModified));
                    Float totalAmount =Float.parseFloat("0");
                    for(SubSale subSale1 : items){
                        totalAmount+=((subSale1.price/subSale1.quantity)*mapSubSales.get(subSale1.subSaleId));
                    }
                    devolutionTotalTicket.setText("Total: $"+totalAmount);
                }
            }
        });
        return view;
    }
}

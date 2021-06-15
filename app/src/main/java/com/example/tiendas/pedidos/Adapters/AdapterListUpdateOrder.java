package com.example.tiendas.pedidos.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tiendas.R;
import com.example.tiendas.Utils.Models.OrderDetailsToEdit;
import com.example.tiendas.pedidos.view.PedidoDetails;
import com.example.tiendas.pedidos.view.PedidoDetailsContract;
import com.google.android.material.textfield.TextInputLayout;

public class AdapterListUpdateOrder  extends BaseAdapter{


        Context context;
        OrderDetailsToEdit detailsItems[];
        Boolean isEditing;

        PedidoDetailsContract viewClass;
        public AdapterListUpdateOrder(Context appContext, OrderDetailsToEdit[] orderDetailsItems, Boolean isEditing, PedidoDetails view){
            this.context=appContext;
            this.detailsItems=orderDetailsItems;
            this.isEditing=isEditing;

            this.viewClass = view;
        }

        @Override
        public int getCount() {
            return this.detailsItems.length;
        }

    @Override
    public Object getItem(int position) {
        return null;
    }

        /*@Override
        public Object getItem(int position) {
            OrderDetailsToEdit item = new OrderDetailsToEdit();
            item.setName(this.detailsItems[position].getName());
            item.setPresentation(this.detailsItems[position].getPresentation());
            Integer input= this.inputsValues[position];
            System.out.println("Position: "+position);
            if(input!=null) {
                System.out.println("Existe"+inputsValues[position]);

                item.setQuantity(inputsValues[position]);
            }else{
                System.out.println("No existe");
                item.setQuantity(this.detailsItems[position].getQuantity());
            }
            item.setSubOrderId(this.detailsItems[position].getSubOrderId());


            return this.detailsItems[position];
        }*/

        @Override
        public long getItemId(int position) {
            return position;
        }


    TextView nameProduct,presentationProduct,idSubOrder;

    Button delButton;



    @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            View view= layoutInflater.inflate(R.layout.list_order_edit_item, null);
            LinearLayout container = view.findViewById(R.id.container);
            nameProduct = view.findViewById(R.id.productOrder);
            presentationProduct = view.findViewById(R.id.presentationOrder);
            idSubOrder = view.findViewById(R.id.idSubOrder);
            TextInputLayout input = (TextInputLayout) view.findViewById(R.id.orderUpdateInputQuantity);
            delButton= view.findViewById(R.id.delSubOrder);
            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isEditing) {
                        viewClass.deleteItem(position);
                    }
                }
            });
            nameProduct.setText(detailsItems[position].getName());
            presentationProduct.setText(detailsItems[position].getPresentation());
            input.getEditText().setText(detailsItems[position].getQuantity().toString());
            idSubOrder.setText(detailsItems[position].getSubOrderId().toString());
            input.getEditText().setEnabled(isEditing);

            Boolean outOfStock = false;

            System.out.println("Position: "+position);
            for(OrderDetailsToEdit item : detailsItems){
                System.out.println("Boolean: "+item.getOutOfStock());
                if(item.getOutOfStock()==1){
                    outOfStock = true;
                }
            }
            if(outOfStock){
                nameProduct.setTextColor(Color.BLACK);
                presentationProduct.setTextColor(Color.BLACK);
                container.setBackgroundColor(Color.parseColor("#b0a69e"));
                //this.inputs[position]
                input.getEditText().setBackgroundColor(Color.parseColor("#b0a69e"));
            }
            input.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    System.out.println("Value: "+input.getEditText().getText().toString()+ "Position: "+position);
                    if(!input.getEditText().getText().toString().isEmpty()) {
                        viewClass.setValuesToRow(position,Integer.parseInt(input.getEditText().getText().toString()));
                    }
                }
            });
            return  view;
        }
}

package com.example.tiendas.pedidos.view;

import com.example.tiendas.Utils.Models.OrderDetailsToEdit;

public interface PedidoDetailsContract {

    void setDetails(OrderDetailsToEdit[] orderDetailsToEdit);
    void deleteItem(int position);
    void goBack();
    void goToLogin();

    void setValuesToRow(int position,int value);

}

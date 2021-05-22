package com.example.ventasrovianda.pedidos.view;

import com.example.ventasrovianda.Utils.Models.OrderDetailsToEdit;

public interface PedidoDetailsContract {

    void setDetails(OrderDetailsToEdit[] orderDetailsToEdit);
    void deleteItem(int position);
    void goBack();
    void goToLogin();

    void setValuesToRow(int position,int value);

}

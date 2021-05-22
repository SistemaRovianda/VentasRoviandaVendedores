package com.example.ventasrovianda.pedidos.view;

import com.example.ventasrovianda.Utils.Models.ProductRoviandaToSale;

public interface PedidoFormContract {

    void goToPedidoView();

    void setProductCodeError(String msg);
    void setQuantityProductError(String msg);

    void addProductToPedido(ProductRoviandaToSale productRoviandaToSale);
    void genericMessage(String title,String msg);
    void registerSuccess();
    void registerError();

    void removeItemOfList(int position);

}

package com.example.tiendas.pedidos.presenter;

import com.example.tiendas.Utils.Models.OrderDTO;

public interface PedidoFormPresenterContract {
    void findProduct(String productId,String quantity);
    void registerOrder(OrderDTO orderDTO);
}

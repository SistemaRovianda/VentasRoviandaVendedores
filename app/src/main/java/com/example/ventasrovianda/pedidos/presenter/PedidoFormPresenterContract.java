package com.example.ventasrovianda.pedidos.presenter;

import com.example.ventasrovianda.Utils.Models.OrderDTO;

public interface PedidoFormPresenterContract {
    void findProduct(String productId,String quantity);
    void registerOrder(OrderDTO orderDTO,String uid);
}

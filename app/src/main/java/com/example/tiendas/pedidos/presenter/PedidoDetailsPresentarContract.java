package com.example.tiendas.pedidos.presenter;

import com.example.tiendas.Utils.Models.UpdateOrderRequest;

import java.util.List;

public interface PedidoDetailsPresentarContract {

    void getOrderDetailsToEdit(Long orderId);
    void udpateOrderDetails(List<UpdateOrderRequest> request, Long orderId);
    void logout();
}

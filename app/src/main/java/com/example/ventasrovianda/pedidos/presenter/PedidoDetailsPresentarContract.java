package com.example.ventasrovianda.pedidos.presenter;

import com.example.ventasrovianda.Utils.Models.UpdateOrderRequest;

import java.util.List;

public interface PedidoDetailsPresentarContract {

    void getOrderDetailsToEdit(Long orderId);
    void udpateOrderDetails(List<UpdateOrderRequest> request, Long orderId);
    void logout();
}

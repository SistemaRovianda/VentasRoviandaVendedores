package com.example.ventasrovianda.pedidos.presenter;

public interface PedidoPresenterContract {

    void getOrders(String uid);

    void getOrderDetailsToEditToPrint(Long orderId);

    void getDetailsPresentation(int orderId,int productId);

    void logout();
}

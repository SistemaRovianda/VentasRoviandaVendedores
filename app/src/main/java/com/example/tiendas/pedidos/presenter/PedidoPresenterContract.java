package com.example.tiendas.pedidos.presenter;

public interface PedidoPresenterContract {

    void getOrders();

    void getOrderDetailsToEditToPrint(Long orderId);

    void getDetailsPresentation(int orderId,int productId);

    void logout();
}

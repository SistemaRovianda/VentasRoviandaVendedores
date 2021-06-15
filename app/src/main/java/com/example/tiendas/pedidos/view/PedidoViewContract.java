package com.example.tiendas.pedidos.view;

import com.example.tiendas.Utils.Models.OrderDTO;
import com.example.tiendas.Utils.Models.OrderDetails;
import com.example.tiendas.Utils.Models.OrderPresentationDetails;

public interface PedidoViewContract {

    void setOrders(OrderDTO[] orders);
    void goToForm();

    void showDetails(OrderDetails[] orderDetails,int orderId);
    void genericMessage(String title,String msg);
    void showPresetationDetails(OrderPresentationDetails[] orderPresentationDetails);
    void goToLogin();

    void printTicketOrder(String ticket);
}

package com.example.ventasrovianda.pedidos.view;

import com.example.ventasrovianda.Utils.Models.OrderDTO;
import com.example.ventasrovianda.Utils.Models.OrderDetails;
import com.example.ventasrovianda.Utils.Models.OrderDetailsToEdit;
import com.example.ventasrovianda.Utils.Models.OrderPresentationDetails;

public interface PedidoViewContract {

    void setOrders(OrderDTO[] orders);
    void goToForm();

    void showDetails(OrderDetails[] orderDetails,int orderId);
    void genericMessage(String title,String msg);
    void showPresetationDetails(OrderPresentationDetails[] orderPresentationDetails);
    void goToLogin();

    void printTicketOrder(String ticket);
}

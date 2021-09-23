package com.example.ventasrovianda.sales.presenter;

import com.example.ventasrovianda.Utils.Models.PayDebtsModel;
import com.example.ventasrovianda.Utils.Models.SaleResponseDTO;

public interface SalePresenterContract {

    void getAllSaleOfDay();
    void cancelSale(Long saleId);
    void cancelSaleOffline(String folio);
    void getTicket(Long ticketId);
    void getResguardedTicket();
    void verifyConnectionPrinterToGetTicket(Long ticketId);
    void verifyConnectionPrinterToGetTicketOffline(String folio);
    void logout();

    void getAllSaleDebtsOfDay();

    void doPayDebt(Long saleId, PayDebtsModel payDebtsModel);
    void checkPayDeb(SaleResponseDTO sale);
    void reprintPaydeb(SaleResponseDTO sale);

}

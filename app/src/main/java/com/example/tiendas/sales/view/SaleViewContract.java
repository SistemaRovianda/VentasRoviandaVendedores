package com.example.tiendas.sales.view;

import com.example.tiendas.Utils.Models.SaleResponseDTO;
import com.example.tiendas.Utils.Models.TotalSoldedDTO;

public interface SaleViewContract {

    void setSalesOfDay(SaleResponseDTO[] sales);
    void setSalesDebtsOfDay(SaleResponseDTO[] sales);


    void genericMessage(String title,String msg);
    void checkPrinterConnection(Long ticketId);
    void checkPrinterConnectionOffline(String folio);
    void reprintTicket(String ticket);
    //void findPrinter(Set<BluetoothDevice> devices);
    void goToLogin();
    void setLoading(boolean loading);
    void printTiket(String ticket);

    void checkPaydeb(SaleResponseDTO sale);

    void setAcumulated(TotalSoldedDTO totalSoldedDTO);

    void cancelSale(String folio);

    void printTicketSale(String folio);
    boolean isNetworkAvailable();
}

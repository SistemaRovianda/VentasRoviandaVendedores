package com.example.ventasrovianda.sales.view;

import android.bluetooth.BluetoothDevice;

import com.example.ventasrovianda.Utils.Models.SaleResponseDTO;
import com.example.ventasrovianda.Utils.Models.TotalSoldedDTO;

import java.util.Set;

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

    void goToDevolutionSaleToView(String folio);

    void printTicketSale(String folio);
    boolean isNetworkAvailable();

    void showOptionsSale(SaleResponseDTO sale);
}

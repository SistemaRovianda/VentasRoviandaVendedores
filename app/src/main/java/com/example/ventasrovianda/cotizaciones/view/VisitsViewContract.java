package com.example.ventasrovianda.cotizaciones.view;

import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.ClientOfflineMode;
import com.example.ventasrovianda.Utils.Models.ClientVisitDTO;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.Models.ModeOfflineNewVersion;
import com.example.ventasrovianda.Utils.Models.ProductPresentation;
import com.example.ventasrovianda.Utils.Models.ProductRovianda;
import com.example.ventasrovianda.Utils.Models.SincronizationResponse;
import com.example.ventasrovianda.Utils.Models.SincronizeSingleSaleSuccess;
import com.example.ventasrovianda.clientsv2.models.ClientV2Response;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateResponse;
import com.example.ventasrovianda.clientsv2.models.ClientV2VisitResponse;
import com.example.ventasrovianda.cotizaciones.models.SaleCreditPayedResponse;

import java.util.List;

public interface VisitsViewContract {

    void setClientVisits(String dayStr,String hint,Boolean filtered);

    void showPresentationProduct(List<ProductPresentation> presentations,String presentationName);

    void reloadVisits();

    void genericMessage(String title,String msg);

    void setClientVisited(ClientDTO clientVisited);
    void goToLogin();
    void goToHome();

    void sincronizeComplete();
    void sincronizeError();
    void setModeOffline(ModeOfflineNewVersion modeOffline);
    void modalSincronizationStart(String msg);
    void modalSincronizationEnd();

    void showNotificationSincronization(String msg);
    void hiddeNotificationSincronizastion();
    void completeSincronzation(SincronizationResponse sincronizationResponse);

    void modalMessageOperation(String msg);

    void setClientsRegisters(List<ClientV2Response> clientsRegistered);
    void setClientsUpdated(List<ClientV2UpdateResponse> clientsUpdated);
    void setClientVisitedRegistered(List<ClientV2VisitResponse> clientV2Visit);

    void setUploadingStatus(boolean flag);
    void setAllSalesCreditPaymentStatus(List<SaleCreditPayedResponse> payments);

    void firstStep();
    void secondStep();
    void thirdStep();
    void checkSalesUnSincronized();
}


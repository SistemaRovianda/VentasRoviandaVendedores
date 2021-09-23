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
    void modalSincronizationStart();
    void modalSincronizationEnd();

    void showNotificationSincronization(String msg);
    void hiddeNotificationSincronizastion();
    void completeSincronzation(SincronizationResponse sincronizationResponse);
}


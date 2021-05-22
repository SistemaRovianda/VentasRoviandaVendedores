package com.example.ventasrovianda.cotizaciones.view;

import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.ClientOfflineMode;
import com.example.ventasrovianda.Utils.Models.ClientVisitDTO;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.Models.ProductPresentation;
import com.example.ventasrovianda.Utils.Models.ProductRovianda;

import java.util.List;

public interface VisitsViewContract {

    void setClientVisits(ClientOfflineMode[] clientsVisits);

    void showPresentationProduct(List<ProductPresentation> presentations,String presentationName);

    void reloadVisits();

    void genericMessage(String title,String msg);

    void setClientVisited(ClientDTO clientVisited);
    void goToLogin();
    void goToHome();

    void setModeOffline(ModeOfflineModel modeOffline);
    void sincronizeComplete();
    void sincronizeError();
}


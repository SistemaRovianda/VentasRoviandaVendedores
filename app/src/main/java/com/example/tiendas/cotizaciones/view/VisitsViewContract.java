package com.example.tiendas.cotizaciones.view;

import com.example.tiendas.Utils.Models.ClientDTO;
import com.example.tiendas.Utils.Models.ClientOfflineMode;
import com.example.tiendas.Utils.Models.ModeOfflineModel;
import com.example.tiendas.Utils.Models.ProductPresentation;

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


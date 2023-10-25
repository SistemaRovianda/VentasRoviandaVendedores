package com.example.ventasrovianda.clientsv2.view;


import com.example.ventasrovianda.Utils.Models.AddressCoordenatesResponse;
import com.example.ventasrovianda.clientsv2.models.ClientV2Response;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateResponse;

import java.util.List;

public interface ClientGeneralDataRegisterViewContract {
    void setAddressByCoordenates(AddressCoordenatesResponse addressCoordenatesResponse,Double latitude,Double longitude);
    void cannotTakeCoordenatesInfo();
    void goBack();
    void goToMap();
    void closeModalRegisteringUpdating();
    void showModalCoordenatesSuccessError(String title,String msg);
    void updateClientRegisteredInServer(ClientV2Response clientV2Response);
    void failConnectionService();
    void updateClientInServer(List<ClientV2UpdateResponse> clientV2UpdateResponse);
    void showModalSuccess(String title,String msg);
}

package com.example.ventasrovianda.clientsv2.presenter;

import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.clientsv2.models.ClientV2Request;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateRequest;

import java.util.List;

public interface ClientGeneralDataRegisterPresenterContract {
    void getAddressByCoordenates(Double longitude,Double latitude);
    void tryRegisterClient(ClientV2Request clientV2Request);
    void updateCustomerV2(List<ClientV2UpdateRequest> clientV2UpdateRequestList);
}

package com.example.ventasrovianda.cotizaciones.view;

import com.example.ventasrovianda.cotizaciones.models.ClientVisitListItem;

public interface VisitsMapViewContract {
    void genericMessage(String title,String msg);
    void goBack();
    void setSelection(int index,boolean isVisited,boolean availableForVisitRecord);
}

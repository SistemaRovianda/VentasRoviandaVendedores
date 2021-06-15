package com.example.tiendas.clients.view;

public interface RegisterClientViewContract {

    void setNombreError(String msg);
    void setClaveError(String msg);
    void setTelefonoError(String msg);
    void setRfcError(String msg);
    void setCalleError(String msg);
    void setNoExtError(String msg);
    void setNoIntError(String msg);
    void setExtreCalleError(String msg);
    void setYCalleError(String msg);
    void setKeyClientText(String msg);
    void setColoniaError(String msg);
    void setReferenciaError(String msg);
    void setLocalicadError(String msg);
    void setMunicipioError(String msg);
    void setCpError(String msg);
    void setEstadoError(String msg);
    void registroCompleto(String clave);
    void registroFallido();
    void genericMessage(String title,String msg);
}

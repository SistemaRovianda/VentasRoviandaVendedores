package com.example.tiendas.Utils;

import androidx.lifecycle.ViewModel;

import com.example.tiendas.Utils.Models.ModeOfflineModel;

public class ViewModelStore extends ViewModel {

    private ModeOfflineModel modeOfflineModel;
    public ModeOfflineModel getStore(){
        return  this.modeOfflineModel;
    }

    public  void saveStore(ModeOfflineModel modeOfflineModel){
        this.modeOfflineModel = modeOfflineModel;
    }

}

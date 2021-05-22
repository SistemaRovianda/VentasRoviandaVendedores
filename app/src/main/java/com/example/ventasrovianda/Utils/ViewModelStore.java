package com.example.ventasrovianda.Utils;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;

public class ViewModelStore extends ViewModel {

    private ModeOfflineModel modeOfflineModel;
    public ModeOfflineModel getStore(){
        return  this.modeOfflineModel;
    }

    public  void saveStore(ModeOfflineModel modeOfflineModel){
        this.modeOfflineModel = modeOfflineModel;
    }

}

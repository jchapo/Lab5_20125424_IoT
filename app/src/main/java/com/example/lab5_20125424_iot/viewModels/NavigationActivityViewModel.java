package com.example.lab5_20125424_iot.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lab5_20125424_iot.items.ListElementTarea;


import java.util.ArrayList;

public class NavigationActivityViewModel extends ViewModel {

    private MutableLiveData<ArrayList<ListElementTarea>> listaTareas = new MutableLiveData<>();
    private MutableLiveData<String> nombreUsuario = new MutableLiveData<>();

    public MutableLiveData<ArrayList<ListElementTarea>> getListaTareas() {
        return listaTareas;
    }

    public void setListaTareas(MutableLiveData<ArrayList<ListElementTarea>> listaTareas) {
        this.listaTareas = listaTareas;
    }

    public MutableLiveData<String> getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(MutableLiveData<String> nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}

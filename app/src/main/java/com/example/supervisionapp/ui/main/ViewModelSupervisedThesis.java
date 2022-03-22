package com.example.supervisionapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.supervisionapp.data.list.model.SupervisedThesesListItem;

import java.util.List;

public class ViewModelSupervisedThesis extends ViewModel {
    private MutableLiveData<List<SupervisedThesesListItem>> supervisedTheses = new MutableLiveData<>();

    public LiveData<List<SupervisedThesesListItem>> getSupervisedTheses() {
        return supervisedTheses;
    }

    public void setSupervisedTheses(List<SupervisedThesesListItem> theses) {
        supervisedTheses.postValue(theses);
    }
}

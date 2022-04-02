package com.example.supervisionapp.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.supervisionapp.persistence.Thesis;

public class ViewModelSubmitThesis extends ViewModel {
    private MutableLiveData<Thesis> thesis = new MutableLiveData<>();

    public MutableLiveData<Thesis> getThesis() {
        return thesis;
    }

    public void setThesis(Thesis thesis) {
        this.thesis.postValue(thesis);
    }
}

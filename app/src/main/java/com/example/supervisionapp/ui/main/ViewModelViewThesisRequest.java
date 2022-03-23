package com.example.supervisionapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.supervisionapp.data.model.ThesisModel;

public class ViewModelViewThesisRequest extends ViewModel {
    private MutableLiveData<ThesisModel> thesis = new MutableLiveData<>();

    public LiveData<ThesisModel> getThesis() {
        return thesis;
    }

    public void setThesis(ThesisModel thesis) {
        this.thesis.postValue(thesis);
    }
}

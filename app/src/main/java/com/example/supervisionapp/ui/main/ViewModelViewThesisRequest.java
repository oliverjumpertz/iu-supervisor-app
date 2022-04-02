package com.example.supervisionapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.supervisionapp.data.model.SupervisionRequestModel;
import com.example.supervisionapp.data.model.ThesisModel;

public class ViewModelViewThesisRequest extends ViewModel {
    private MutableLiveData<SupervisionRequestModel> supervisionRequest = new MutableLiveData<>();

    public LiveData<SupervisionRequestModel> getSupervisionRequest() {
        return supervisionRequest;
    }

    public void setSupervisionRequest(SupervisionRequestModel supervisionRequest) {
        this.supervisionRequest.postValue(supervisionRequest);
    }
}

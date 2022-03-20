package com.example.supervisionapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.supervisionapp.data.list.model.AdvertisedThesesListItem;

import java.util.List;

public class ViewModelMyResearch extends ViewModel {
    private MutableLiveData<List<AdvertisedThesesListItem>> advertisedTheses = new MutableLiveData<>();

    public LiveData<List<AdvertisedThesesListItem>> getAdvertisedTheses() {
        return advertisedTheses;
    }

    public void setAdvertisedTheses(List<AdvertisedThesesListItem> theses) {
        advertisedTheses.postValue(theses);
    }
}

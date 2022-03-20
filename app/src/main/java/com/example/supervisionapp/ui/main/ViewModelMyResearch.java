package com.example.supervisionapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.supervisionapp.data.list.model.MyResearchListItem;

import java.util.List;

public class ViewModelMyResearch extends ViewModel {
    private MutableLiveData<List<MyResearchListItem>> advertisedTheses = new MutableLiveData<>();

    public LiveData<List<MyResearchListItem>> getMyResearch() {
        return advertisedTheses;
    }

    public void setMyResearch(List<MyResearchListItem> theses) {
        advertisedTheses.postValue(theses);
    }
}

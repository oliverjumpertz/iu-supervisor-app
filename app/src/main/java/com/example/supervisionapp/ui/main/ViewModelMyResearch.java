package com.example.supervisionapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.supervisionapp.data.list.model.MyResearchListItem;

import java.util.List;

public class ViewModelMyResearch extends ViewModel {
    private MutableLiveData<List<MyResearchListItem>> myResearchTheses = new MutableLiveData<>();

    public LiveData<List<MyResearchListItem>> getMyResearchTheses() {
        return myResearchTheses;
    }

    public void setMyResearchTheses(List<MyResearchListItem> theses) {
        myResearchTheses.postValue(theses);
    }
}

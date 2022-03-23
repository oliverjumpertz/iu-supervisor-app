package com.example.supervisionapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.supervisionapp.data.list.model.ThesesRequestsListItem;

import java.util.List;

public class ViewModelThesesRequests extends ViewModel {
    private MutableLiveData<List<ThesesRequestsListItem>> thesesRequests = new MutableLiveData<>();

    public LiveData<List<ThesesRequestsListItem>> getThesesRequests() {
        return thesesRequests;
    }

    public void setThesesRequests(List<ThesesRequestsListItem> theses) {
        thesesRequests.postValue(theses);
    }
}

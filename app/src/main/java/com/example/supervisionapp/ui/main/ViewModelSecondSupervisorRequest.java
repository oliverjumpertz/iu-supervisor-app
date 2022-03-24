package com.example.supervisionapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.data.model.User;

import java.util.List;

public class ViewModelSecondSupervisorRequest extends ViewModel {
    private MutableLiveData<ThesisModel> thesis = new MutableLiveData<>();
    private MutableLiveData<List<User>> users = new MutableLiveData<>();

    public LiveData<ThesisModel> getThesis() {
        return thesis;
    }

    public void setThesis(ThesisModel thesis) {
        this.thesis.postValue(thesis);
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users.postValue(users);
    }
}

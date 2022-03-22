package com.example.supervisionapp.data.list.model;

import com.example.supervisionapp.data.model.SupervisoryType;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;

import java.util.Objects;

public class SupervisedThesesListItem {
    private final String title;
    private final String student;
    private final SupervisoryTypeModel supervisoryType;

    public SupervisedThesesListItem(String title, String student, SupervisoryTypeModel supervisoryType) {
        this.title = title;
        this.student = student;
        this.supervisoryType = supervisoryType;
    }

    public String getTitle() {
        return title;
    }

    public String getStudent() {
        return student;
    }

    public SupervisoryTypeModel getSupervisoryType() {
        return supervisoryType;
    }
}

package com.example.supervisionapp.data.list.model;

import com.example.supervisionapp.data.model.SupervisoryTypeModel;

public class SupervisedThesesListItem {
    private final long thesisId;
    private final String title;
    private final String student;
    private final SupervisoryTypeModel supervisoryType;

    public SupervisedThesesListItem(long thesisId, String title, String student, SupervisoryTypeModel supervisoryType) {
        this.thesisId = thesisId;
        this.title = title;
        this.student = student;
        this.supervisoryType = supervisoryType;
    }

    public long getThesisId() {
        return thesisId;
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

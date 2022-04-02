package com.example.supervisionapp.data.list.model;

import com.example.supervisionapp.data.model.SupervisionRequestTypeModel;

public class ThesesRequestsListItem {
    private final long thesisId;
    private final long userId;
    private final String title;
    private final String name;
    private final SupervisionRequestTypeModel requestType;

    public ThesesRequestsListItem(long thesisId, long userId, String title, String name, SupervisionRequestTypeModel requestType) {
        this.thesisId = thesisId;
        this.userId = userId;
        this.title = title;
        this.name = name;
        this.requestType = requestType;
    }

    public long getThesisId() {
        return thesisId;
    }

    public long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public SupervisionRequestTypeModel getRequestType() {
        return requestType;
    }
}

package com.example.supervisionapp.data.list.model;

import java.util.Objects;

public class SecondSupervisorRequestListItem {
    private final long userId;
    private final long thesisId;
    private final String name;

    public SecondSupervisorRequestListItem(long userId, long thesisId, String name) {
        this.userId = userId;
        this.thesisId = thesisId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getUserId() {
        return userId;
    }

    public long getThesisId() {
        return thesisId;
    }
}

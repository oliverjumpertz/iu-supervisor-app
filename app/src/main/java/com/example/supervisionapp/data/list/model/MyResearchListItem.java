package com.example.supervisionapp.data.list.model;

import java.util.Objects;

public class MyResearchListItem {
    private final long thesisId;
    private final String title;
    private final String description;

    public MyResearchListItem(long thesisId, String title, String description) {
        this.thesisId = thesisId;
        this.title = title;
        this.description = description;
    }

    public long getThesisId() {
        return thesisId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

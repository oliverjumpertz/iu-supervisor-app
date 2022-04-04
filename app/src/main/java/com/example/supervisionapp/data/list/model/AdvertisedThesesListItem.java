package com.example.supervisionapp.data.list.model;

public class AdvertisedThesesListItem {
    private final long thesisId;
    private final String title;
    private final String description;
    private final boolean alreadyRequested;

    public AdvertisedThesesListItem(long thesisId,
                                    String title,
                                    String description,
                                    boolean alreadyRequested) {
        this.thesisId = thesisId;
        this.title = title;
        this.description = description;
        this.alreadyRequested = alreadyRequested;
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

    public boolean isAlreadyRequested() {
        return alreadyRequested;
    }
}

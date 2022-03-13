package com.example.supervisionapp.data.list.model;

import java.util.Objects;

public class AdvertisedThesesListItem {
    private final String title;
    private final String description;

    public AdvertisedThesesListItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvertisedThesesListItem advertisedThesesListItem = (AdvertisedThesesListItem) o;
        return Objects.equals(title, advertisedThesesListItem.title) && Objects.equals(description, advertisedThesesListItem.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }

    @Override
    public String toString() {
        return "AdvertisedThesesListItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

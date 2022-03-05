package com.example.supervisionapp.data.list.model;

import java.util.Objects;

public class ListItem {
    private String title;
    private String description;

    public ListItem(String title, String description) {
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
        ListItem listItem = (ListItem) o;
        return Objects.equals(title, listItem.title) && Objects.equals(description, listItem.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }
}

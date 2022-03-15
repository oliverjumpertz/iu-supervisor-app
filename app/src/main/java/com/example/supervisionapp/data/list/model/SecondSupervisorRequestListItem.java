package com.example.supervisionapp.data.list.model;

import java.util.Objects;

public class SecondSupervisorRequestListItem {
    private final String name;

    public SecondSupervisorRequestListItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecondSupervisorRequestListItem that = (SecondSupervisorRequestListItem) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "SecondSupervisorRequestListItem{" +
                "name='" + name + '\'' +
                '}';
    }
}

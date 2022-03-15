package com.example.supervisionapp.data.list.model;

import com.example.supervisionapp.data.model.SupervisoryType;

import java.util.Objects;

public class SupervisedThesesListItem {
    private final String title;
    private final String student;
    private final SupervisoryType supervisoryType;

    public SupervisedThesesListItem(String title, String student, SupervisoryType supervisoryType) {
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

    public SupervisoryType getSupervisoryType() {
        return supervisoryType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupervisedThesesListItem that = (SupervisedThesesListItem) o;
        return Objects.equals(title, that.title) && Objects.equals(student, that.student) && supervisoryType == that.supervisoryType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, student, supervisoryType);
    }

    @Override
    public String toString() {
        return "SupervisedThesesListItem{" +
                "title='" + title + '\'' +
                ", student='" + student + '\'' +
                ", supervisoryType=" + supervisoryType +
                '}';
    }
}

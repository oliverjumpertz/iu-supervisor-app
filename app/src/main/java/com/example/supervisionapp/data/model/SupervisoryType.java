package com.example.supervisionapp.data.model;

public enum SupervisoryType {
    FIRST_SUPERVISOR("1."), SECOND_SUPERVISOR("2.");

    private final String textRepresentation;

    SupervisoryType(String textRepresentation) {
        this.textRepresentation = textRepresentation;
    }

    public String getTextRepresentation() {
        return textRepresentation;
    }

    @Override
    public String toString() {
        return "SupervisoryType{" +
                "textRepresentation='" + textRepresentation + '\'' +
                '}';
    }
}

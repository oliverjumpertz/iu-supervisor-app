package com.example.supervisionapp.data.model;

public enum SupervisoryType {
    FIRST_SUPERVISOR((byte) 1), SECOND_SUPERVISOR((byte) 2);

    private final byte type;

    private SupervisoryType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}

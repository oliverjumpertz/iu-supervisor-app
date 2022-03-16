package com.example.supervisionapp.persistence;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SupervisoryType {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String type;
}

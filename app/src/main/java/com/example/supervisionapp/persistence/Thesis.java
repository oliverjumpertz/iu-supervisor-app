package com.example.supervisionapp.persistence;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = ThesisState.class, parentColumns = "id", childColumns = "state")})
public class Thesis {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;

    public String subtitle;

    public String description;

    public String expose;

    public int state;
}

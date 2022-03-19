package com.example.supervisionapp.persistence;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserType {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String type;
}

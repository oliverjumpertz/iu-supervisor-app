package com.example.supervisionapp.persistence;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = UserType.class, parentColumns = "id", childColumns = "type")})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;

    public String name;

    public String foreName;

    public int type;
}

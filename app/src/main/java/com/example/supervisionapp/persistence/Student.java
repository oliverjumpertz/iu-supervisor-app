package com.example.supervisionapp.persistence;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"user", "thesis"},
foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user"),
        @ForeignKey(entity = Thesis.class, parentColumns = "id", childColumns = "thesis")
})
public class Student {
    public int user;

    public int thesis;
}

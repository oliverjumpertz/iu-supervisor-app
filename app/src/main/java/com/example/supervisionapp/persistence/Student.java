package com.example.supervisionapp.persistence;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(primaryKeys = {"user", "thesis"},
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user"),
                @ForeignKey(entity = Thesis.class, parentColumns = "id", childColumns = "thesis")
        }, indices = {
        @Index("user"),
        @Index("thesis"),
        @Index(value = {"user", "thesis"}, unique = true)
})
public class Student {
    public long user;
    public long thesis;
}

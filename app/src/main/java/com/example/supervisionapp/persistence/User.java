package com.example.supervisionapp.persistence;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = UserType.class, parentColumns = "id", childColumns = "type")
}, indices = {
        @Index(value = "username", unique = true),
        @Index("type")
})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;

    public String password;

    public String title;

    public String name;

    public String foreName;

    public int type;
}

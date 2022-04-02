package com.example.supervisionapp.persistence;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(primaryKeys = {"thesis", "user"},
        foreignKeys = {
    @ForeignKey(entity = SupervisionRequestType.class, parentColumns = "id", childColumns = "type")
}, indices = {
        @Index("type"),
        @Index(value = {"thesis", "user"}, unique = true)
})
public class SupervisionRequest {
    public long thesis;
    public long user;
    public long type;
    public String subtitle;
    public String description;
    public String expose;
}

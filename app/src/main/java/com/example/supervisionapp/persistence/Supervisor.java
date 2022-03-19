package com.example.supervisionapp.persistence;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(primaryKeys = {"user", "thesis"}, foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user"),
        @ForeignKey(entity = Thesis.class, parentColumns = "id", childColumns = "thesis"),
        @ForeignKey(entity = SupervisoryType.class, parentColumns = "id", childColumns = "type"),
        @ForeignKey(entity = InvoiceState.class, parentColumns = "id", childColumns = "invoiceState"),
        @ForeignKey(entity = SupervisoryState.class, parentColumns = "id", childColumns = "state")
}, indices = {
        @Index("user"),
        @Index("thesis"),
        @Index(value = {"user", "thesis"}, unique = true),
        @Index("type"),
        @Index("invoiceState"),
        @Index("state")
})
public class Supervisor {
    public int user;

    public int thesis;

    public int type;

    public int invoiceState;

    public int state;
}

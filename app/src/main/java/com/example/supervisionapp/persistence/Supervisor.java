package com.example.supervisionapp.persistence;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"user", "thesis"}, foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user"),
        @ForeignKey(entity = Thesis.class, parentColumns = "id", childColumns = "thesis"),
        @ForeignKey(entity = SupervisoryType.class, parentColumns = "id", childColumns = "supervisoryType"),
        @ForeignKey(entity = InvoiceState.class, parentColumns = "id", childColumns = "invoiceState"),
        @ForeignKey(entity = SupervisoryState.class, parentColumns = "id", childColumns = "state")
})
public class Supervisor {
    public int user;

    public int thesis;

    public int type;

    public int invoiceState;

    public int state;
}

package com.example.supervisionapp.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(primaryKeys = {"user", "thesis"}, foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user"),
        @ForeignKey(entity = Thesis.class, parentColumns = "id", childColumns = "thesis"),
        @ForeignKey(entity = SupervisoryType.class, parentColumns = "id", childColumns = "type"),
        @ForeignKey(entity = InvoiceState.class, parentColumns = "id", childColumns = "invoice_state"),
        @ForeignKey(entity = SupervisoryState.class, parentColumns = "id", childColumns = "state")
}, indices = {
        @Index("user"),
        @Index("thesis"),
        @Index(value = {"thesis", "type"}, unique = true),
        @Index(value = {"user", "thesis"}, unique = true),
        @Index("type"),
        @Index("invoice_state"),
        @Index("state")
})
public class Supervisor {
    public long user;
    public long thesis;
    public long type;
    @ColumnInfo(name = "invoice_state")
    public long invoiceState;
    public long state;
}

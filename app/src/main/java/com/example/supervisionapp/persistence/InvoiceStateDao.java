package com.example.supervisionapp.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface InvoiceStateDao {
    @Query("SELECT * FROM INVOICESTATE")
    Single<List<InvoiceState>> getAll();

    @Query("SELECT * FROM INVOICESTATE WHERE id = :id")
    Single<InvoiceState> getById(int id);

    @Query("SELECT * FROM INVOICESTATE WHERE state = :state")
    Single<InvoiceState> getByType(String state);

    @Insert
    Single<Void> insert(InvoiceState invoiceState);

    @Update
    Single<Void> update(InvoiceState invoiceState);

    @Delete
    Single<Void> delete(InvoiceState invoiceState);
}

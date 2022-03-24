package com.example.supervisionapp.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface InvoiceStateDao {
    @Query("SELECT * FROM INVOICESTATE")
    Single<List<InvoiceState>> getAll();

    @Query("SELECT * FROM INVOICESTATE WHERE id = :id")
    Maybe<InvoiceState> getById(long id);

    @Query("SELECT * FROM INVOICESTATE WHERE state = :state")
    Single<InvoiceState> getByState(String state);

    @Insert
    Single<Long> insert(InvoiceState invoiceState);

    @Update
    Completable update(InvoiceState invoiceState);

    @Delete
    Completable delete(InvoiceState invoiceState);
}

package com.example.supervisionapp.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface ThesisStateDao {
    @Query("SELECT * FROM THESISSTATE")
    Single<List<ThesisState>> getAll();

    @Query("SELECT * FROM THESISSTATE WHERE id = :id")
    Single<ThesisState> getById(long id);

    @Query("SELECT * FROM THESISSTATE WHERE state = :state")
    Single<ThesisState> getByState(String state);

    @Insert
    Single<Long> insert(ThesisState thesisState);

    @Update
    Single<Void> update(ThesisState thesisState);

    @Delete
    Single<Void> delete(ThesisState thesisState);
}

package com.example.supervisionapp.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface ThesisDao {
    @Query("SELECT * FROM THESIS")
    Single<List<Thesis>> getAll();

    @Query("SELECT * FROM THESIS WHERE id = :id")
    Single<Thesis> getById(int id);

    @Insert
    Single<Void> insert(Thesis thesis);

    @Update
    Single<Void> update(Thesis thesis);

    @Delete
    Single<Void> delete(Thesis thesis);
}

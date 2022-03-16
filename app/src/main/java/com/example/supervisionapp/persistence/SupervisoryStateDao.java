package com.example.supervisionapp.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface SupervisoryStateDao {
    @Query("SELECT * FROM SUPERVISORYSTATE")
    Single<List<SupervisoryState>> getAll();

    @Query("SELECT * FROM SUPERVISORYSTATE WHERE id = :id")
    Single<SupervisoryState> getById(int id);

    @Query("SELECT * FROM SUPERVISORYSTATE WHERE state = :state")
    Single<SupervisoryState> getByType(String state);

    @Insert
    Single<Void> insert(SupervisoryState supervisoryState);

    @Update
    Single<Void> update(SupervisoryState supervisoryState);

    @Delete
    Single<Void> delete(SupervisoryState supervisoryState);
}

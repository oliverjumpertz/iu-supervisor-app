package com.example.supervisionapp.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SupervisoryTypeDao {
    @Query("SELECT * FROM SUPERVISORYTYPE")
    Single<List<SupervisoryType>> getAll();

    @Query("SELECT * FROM SUPERVISORYTYPE WHERE id = :id")
    Single<SupervisoryType> getById(long id);

    @Query("SELECT * FROM SUPERVISORYTYPE WHERE type = :type")
    Single<SupervisoryType> getByType(String type);

    @Insert
    Single<Long> insert(SupervisoryType supervisoryType);

    @Update
    Completable update(SupervisoryType supervisoryType);

    @Delete
    Completable delete(SupervisoryType supervisoryType);
}

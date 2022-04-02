package com.example.supervisionapp.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SupervisionRequestTypeDao {
    @Query("SELECT * FROM SupervisionRequestType WHERE id = :id")
    Maybe<SupervisionRequestType> getById(long id);

    @Query("SELECT * FROM SupervisionRequestType WHERE type = :type")
    Maybe<SupervisionRequestType> getByType(String type);

    @Insert
    Single<Long> insert(SupervisionRequestType supervisionRequestType);

    @Update
    Completable update(SupervisionRequestType supervisionRequestType);

    @Delete
    Completable delete(SupervisionRequestType supervisionRequestType);
}

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
public interface UserTypeDao {
    @Query("SELECT * FROM USERTYPE")
    Single<List<UserType>> getAll();

    @Query("SELECT * FROM USERTYPE WHERE id = :id")
    Single<UserType> getById(int id);

    @Query("SELECT * FROM USERTYPE WHERE type = :type")
    Single<UserType> getByType(String type);

    @Insert
    Completable insert(UserType userType);

    @Update
    Completable update(UserType userType);

    @Delete
    Completable delete(UserType userType);

    @Query("DELETE FROM USERTYPE")
    Completable deleteAll();
}

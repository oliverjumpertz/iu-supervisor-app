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
public interface UserDao {
    @Query("SELECT * FROM USER")
    Single<List<User>> getAll();

    @Query("SELECT * FROM USER WHERE id = :id")
    Single<User> getById(int id);

    @Query("SELECT * FROM USER WHERE username = :username")
    Single<User> getByUsername(String username);

    @Insert
    Completable insert(User user);

    @Update
    Completable update(User user);

    @Delete
    Completable delete(User user);

    @Query("DELETE FROM USER")
    Completable deleteAll();
}

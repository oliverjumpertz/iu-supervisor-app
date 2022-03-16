package com.example.supervisionapp.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {
    @Query("SELECT * FROM USER")
    Single<List<User>> getAll();

    @Query("SELECT * FROM USER WHERE id = :id")
    Single<User> getById(int id);

    @Insert
    Single<Void> insert(User user);

    @Update
    Single<Void> update(User user);

    @Delete
    Single<Void> delete(User user);
}

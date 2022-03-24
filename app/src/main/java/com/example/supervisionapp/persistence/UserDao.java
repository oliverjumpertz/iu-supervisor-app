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
public interface UserDao {
    @Query("SELECT * FROM USER")
    Single<List<User>> getAll();

    @Query("SELECT * FROM USER WHERE id = :id")
    Maybe<User> getById(long id);

    @Query("SELECT * FROM USER WHERE username = :username")
    Maybe<User> getByUsername(String username);

    @Query("SELECT * FROM USER WHERE id IN (:ids)")
    Maybe<List<User>> getByIds(List<Long> ids);

    @Insert
    Single<Long> insert(User user);

    @Update
    Completable update(User user);

    @Delete
    Completable delete(User user);

    @Query("DELETE FROM USER")
    Completable deleteAll();
}

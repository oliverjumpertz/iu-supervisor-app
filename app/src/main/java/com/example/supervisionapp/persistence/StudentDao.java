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
public interface StudentDao {
    @Query("SELECT * FROM STUDENT")
    Single<List<Student>> getAll();

    @Query("SELECT * FROM STUDENT WHERE user = :user AND thesis = :thesis")
    Maybe<Student> getByUserAndThesis(long user, long thesis);

    @Query("SELECT * FROM STUDENT WHERE thesis = :thesis")
    Maybe<Student> getByThesis(long thesis);

    @Query("SELECT * FROM STUDENT WHERE user = :user")
    Maybe<List<Student>> getByUser(long user);

    @Insert
    Completable insert(Student student);

    @Update
    Completable update(Student student);

    @Delete
    Completable delete(Student student);
}

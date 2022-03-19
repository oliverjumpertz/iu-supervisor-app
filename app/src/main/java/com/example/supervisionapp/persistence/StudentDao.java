package com.example.supervisionapp.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface StudentDao {
    @Query("SELECT * FROM STUDENT")
    Single<List<Student>> getAll();

    @Query("SELECT * FROM STUDENT WHERE user = :user AND thesis = :thesis")
    Single<Student> getByUserAndThesis(long user, long thesis);

    @Query("SELECT * FROM STUDENT WHERE thesis = :thesis")
    Single<List<Student>> getByThesis(long thesis);

    @Insert
    Single<Void> insert(Student student);

    @Update
    Single<Void> update(Student student);

    @Delete
    Single<Void> delete(Student student);
}

package com.example.supervisionapp.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface SupervisorDao {
    @Query("SELECT * FROM SUPERVISOR")
    Single<List<Supervisor>> getAll();

    @Query("SELECT * FROM SUPERVISOR WHERE user = :user AND thesis = :thesis")
    Single<Supervisor> getByUserAndThesis(int user, int thesis);

    @Query("SELECT * FROM SUPERVISOR WHERE thesis = :thesis")
    Single<List<Supervisor>> getByThesis(int thesis);

    @Insert
    Single<Void> insert(Supervisor supervisor);

    @Update
    Single<Void> update(Supervisor supervisor);

    @Delete
    Single<Void> delete(Supervisor supervisor);
}

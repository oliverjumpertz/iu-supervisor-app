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
public interface SupervisorDao {
    @Query("SELECT * FROM SUPERVISOR")
    Single<List<Supervisor>> getAll();

    @Query("SELECT * FROM SUPERVISOR WHERE user = :user AND thesis = :thesis")
    Maybe<Supervisor> getByUserAndThesis(long user, long thesis);

    @Query("SELECT * FROM SUPERVISOR WHERE thesis = :thesis AND type = :type")
    Maybe<Supervisor> getByThesisAndType(long thesis, long type);

    @Query("SELECT * FROM SUPERVISOR WHERE thesis = :thesis")
    Maybe<List<Supervisor>> getByThesis(long thesis);

    @Query("SELECT * FROM SUPERVISOR WHERE user = :user")
    Maybe<List<Supervisor>> getByUser(long user);

    @Query("SELECT * FROM SUPERVISOR WHERE user = :user AND state = :state")
    Maybe<List<Supervisor>> getByUserAndState(long user, long state);

    @Query("SELECT * FROM SUPERVISOR WHERE user <> :user AND thesis = :thesis")
    Maybe<Supervisor> getByThesisWhereUserIsNot(long thesis, long user);

    @Query("SELECT * FROM SUPERVISOR WHERE user <> :user")
    Maybe<List<Supervisor>> getWhereUserIsNot(long user);

    @Insert
    Completable insert(Supervisor supervisor);

    @Update
    Completable update(Supervisor supervisor);

    @Delete
    Completable delete(Supervisor supervisor);

    @Query("DELETE FROM SUPERVISOR WHERE thesis = :thesis")
    Completable deleteByThesisId(long thesis);
}

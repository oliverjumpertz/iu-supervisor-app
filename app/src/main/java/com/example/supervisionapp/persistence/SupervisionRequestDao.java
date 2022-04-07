package com.example.supervisionapp.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

@Dao
public interface SupervisionRequestDao {
    @Query("SELECT * FROM SUPERVISIONREQUEST")
    Maybe<List<SupervisionRequest>> getAll();

    @Query("SELECT * FROM SUPERVISIONREQUEST WHERE thesis in (:theses)")
    Maybe<List<SupervisionRequest>> getByTheses(List<Long> theses);

    @Query("SELECT * FROM SUPERVISIONREQUEST WHERE thesis = :thesis AND user = :user")
    Maybe<SupervisionRequest> getByThesisIdAndUserId(long thesis, long user);

    @Query("SELECT * FROM SUPERVISIONREQUEST WHERE user = :user")
    Maybe<List<SupervisionRequest>> getByUserId(long user);

    @Insert
    Completable insert(SupervisionRequest supervisionRequestType);

    @Update
    Completable update(SupervisionRequest supervisionRequestType);

    @Delete
    Completable delete(SupervisionRequest supervisionRequestType);

    @Query("DELETE FROM SUPERVISIONREQUEST WHERE thesis = :thesis AND type = :type")
    Completable deleteByThesisIdAndType(long thesis, long type);
}

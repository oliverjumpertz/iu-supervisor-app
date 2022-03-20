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
public interface ThesisDao {
    @Query("SELECT * FROM THESIS")
    Single<List<Thesis>> getAll();

    @Query("SELECT * FROM THESIS WHERE id = :id")
    Single<Thesis> getById(long id);

    @Query("SELECT * FROM THESIS WHERE id IN (:ids) AND state = :state")
    Maybe<List<Thesis>> getByIdsAndState(List<Long> ids, long state);

    @Insert
    Single<Long> insert(Thesis thesis);

    @Update
    Completable update(Thesis thesis);

    @Delete
    Completable delete(Thesis thesis);
}

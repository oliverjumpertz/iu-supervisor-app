package com.example.supervisionapp.persistence;

import com.example.supervisionapp.data.model.LoggedInUser;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeSource;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Function3;
import kotlin.Pair;
import kotlin.Triple;

public class ThesisRepository {
    private final AppDatabase appDatabase;

    public ThesisRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public Completable createThesis(String title, String description, LoggedInUser loggedInUser) {
        ThesisStateDao thesisStateDao = appDatabase.thesisStateDao();
        ThesisDao thesisDao = appDatabase.thesisDao();
        SupervisoryTypeDao supervisoryTypeDao = appDatabase.supervisoryTypeDao();
        SupervisorDao supervisorDao = appDatabase.supervisorDao();
        InvoiceStateDao invoiceStateDao = appDatabase.invoiceStateDao();
        SupervisoryStateDao supervisoryStateDao = appDatabase.supervisoryStateDao();

        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                appDatabase.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        ThesisState thesisState = thesisStateDao.getByState("ADVERTISED").blockingGet();
                        Thesis thesis = new Thesis();
                        thesis.title = title;
                        thesis.description = description;
                        thesis.state = thesisState.id;
                        thesis.id = thesisDao.insert(thesis).blockingGet();

                        SupervisoryType supervisoryType = supervisoryTypeDao.getByType("FIRST_SUPERVISOR").blockingGet();
                        InvoiceState invoiceState = invoiceStateDao.getByType("UNFINISHED").blockingGet();
                        SupervisoryState supervisoryState = supervisoryStateDao.getByState("DRAFT").blockingGet();

                        Supervisor supervisor = new Supervisor();
                        supervisor.user = loggedInUser.getUserId();
                        supervisor.thesis = thesis.id;
                        supervisor.state = supervisoryState.id;
                        supervisor.type = supervisoryType.id;
                        supervisor.invoiceState = invoiceState.id;
                        supervisorDao.insert(supervisor).blockingAwait();
                    }
                });
            }
        });
    }

    public Maybe<List<Thesis>> getSupervisorsAdvertisedTheses(LoggedInUser loggedInUser) {
        SupervisorDao supervisorDao = appDatabase.supervisorDao();
        ThesisDao thesisDao = appDatabase.thesisDao();
        ThesisStateDao thesisStateDao = appDatabase.thesisStateDao();
        SupervisoryStateDao supervisoryStateDao = appDatabase.supervisoryStateDao();
        // TODO: needs to be supervisorystate draft

        return supervisoryStateDao
                .getByState("DRAFT")
                .flatMap(new Function<SupervisoryState, MaybeSource<List<Supervisor>>>() {
                    @Override
                    public MaybeSource<List<Supervisor>> apply(SupervisoryState supervisoryState) throws Throwable {
                        return supervisorDao.getByUserAndState(loggedInUser.getUserId(), supervisoryState.id);
                    }
                })
                .flatMap(new Function<List<Supervisor>, MaybeSource<Pair<List<Supervisor>, ThesisState>>>() {
                    @Override
                    public MaybeSource<Pair<List<Supervisor>, ThesisState>> apply(List<Supervisor> supervisors) throws Throwable {
                        return Maybe.zip(Maybe.just(supervisors),
                                thesisStateDao.getByState("ADVERTISED"),
                                new BiFunction<List<Supervisor>, ThesisState, Pair<List<Supervisor>, ThesisState>>() {
                                    @Override
                                    public Pair<List<Supervisor>, ThesisState> apply(List<Supervisor> supervisors, ThesisState thesisState) throws Throwable {
                                        return new Pair<>(supervisors, thesisState);
                                    }
                                });
                    }
                }).flatMap(new Function<Pair<List<Supervisor>, ThesisState>, MaybeSource<? extends List<Thesis>>>() {
                    @Override
                    public MaybeSource<? extends List<Thesis>> apply(Pair<List<Supervisor>, ThesisState> pair) throws Throwable {
                        List<Supervisor> supervisors = pair.getFirst();
                        ThesisState thesisState = pair.getSecond();
                        List<Long> ids = new ArrayList<>(supervisors.size());
                        for (Supervisor supervisor : supervisors) {
                            ids.add(supervisor.thesis);
                        }
                        return thesisDao.getByIdsAndState(ids, thesisState.id);
                    }
                });
    }
}

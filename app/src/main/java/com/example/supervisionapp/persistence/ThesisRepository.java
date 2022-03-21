package com.example.supervisionapp.persistence;

import com.example.supervisionapp.data.model.InvoiceStateModel;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisoryStateModel;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.data.model.ThesisStateModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeSource;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Function6;
import io.reactivex.rxjava3.functions.Function8;
import kotlin.Pair;

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

    public Maybe<ThesisModel> getThesisByIdAndUser(long thesisId, LoggedInUser user) {
        ThesisDao thesisDao = appDatabase.thesisDao();
        SupervisorDao supervisorDao = appDatabase.supervisorDao();
        ThesisStateDao thesisStateDao = appDatabase.thesisStateDao();
        SupervisoryStateDao supervisoryStateDao = appDatabase.supervisoryStateDao();
        SupervisoryTypeDao supervisoryTypeDao = appDatabase.supervisoryTypeDao();
        InvoiceStateDao invoiceStateDao = appDatabase.invoiceStateDao();
        StudentDao studentDao = appDatabase.studentDao();
        UserDao userDao = appDatabase.userDao();
        return thesisDao
                .getById(thesisId)
                .flatMap(new Function<Thesis, MaybeSource<Pair<Thesis, Supervisor>>>() {
                    @Override
                    public MaybeSource<Pair<Thesis, Supervisor>> apply(Thesis thesis) throws Throwable {

                        return Maybe.zip(Maybe.just(thesis),
                                supervisorDao.getByUserAndThesis(user.getUserId(), thesis.id),
                                new BiFunction<Thesis, Supervisor, Pair<Thesis, Supervisor>>() {
                                    @Override
                                    public Pair<Thesis, Supervisor> apply(Thesis thesis, Supervisor supervisor) throws Throwable {
                                        return new Pair<>(thesis, supervisor);
                                    }
                                });
                    }
                })
                .flatMap(new Function<Pair<Thesis, Supervisor>, MaybeSource<ThesisModel>>() {
                    @Override
                    public MaybeSource<ThesisModel> apply(Pair<Thesis, Supervisor> pair) throws Throwable {
                        User defaultUser = new User();
                        defaultUser.id = -1;
                        Single<User> student = studentDao
                                .getByThesis(pair.getFirst().id)
                                .flatMap(new Function<Student, MaybeSource<User>>() {
                                    @Override
                                    public MaybeSource<User> apply(Student student) throws Throwable {
                                        return userDao.getById(student.user);
                                    }
                                }).defaultIfEmpty(defaultUser);
                        Supervisor defaultSupervisor = new Supervisor();
                        defaultSupervisor.thesis = -1;
                        defaultSupervisor.user = -1;
                        Single<Supervisor> secondSupervisor = supervisorDao
                                .getByThesisWhereUserIsNot(thesisId, user.getUserId())
                                .defaultIfEmpty(defaultSupervisor);
                        return Maybe.zip(Maybe.just(pair.getFirst()),
                                Maybe.just(pair.getSecond()),
                                thesisStateDao.getById(pair.getFirst().state).toMaybe(),
                                supervisoryStateDao.getById(pair.getSecond().state).toMaybe(),
                                supervisoryTypeDao.getById(pair.getSecond().type).toMaybe(),
                                invoiceStateDao.getById(pair.getSecond().invoiceState).toMaybe(),
                                student.toMaybe(),
                                secondSupervisor.toMaybe(),
                                new Function8<Thesis, Supervisor, ThesisState, SupervisoryState, SupervisoryType, InvoiceState, User, Supervisor, ThesisModel>() {
                                    @Override
                                    public ThesisModel apply(Thesis thesis, Supervisor supervisor, ThesisState thesisState, SupervisoryState supervisoryState, SupervisoryType supervisoryType, InvoiceState invoiceState, User user, Supervisor secondSupervisor) throws Throwable {
                                        String studentName = "";
                                        if (user.id >= 0) {
                                            studentName = user.foreName + " " + user.name;
                                        }
                                        boolean hasSecondSupervisor = secondSupervisor.user >= 0 && secondSupervisor.thesis >= 0;
                                        return new ThesisModel(thesis.title,
                                                thesis.subtitle,
                                                SupervisoryStateModel.valueOf(supervisoryState.state),
                                                SupervisoryTypeModel.valueOf(supervisoryType.type),
                                                studentName,
                                                thesis.expose,
                                                ThesisStateModel.valueOf(thesisState.state),
                                                hasSecondSupervisor,
                                                InvoiceStateModel.valueOf(invoiceState.state));
                                    }
                                });
                    }
                });
    }
}

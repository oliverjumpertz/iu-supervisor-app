package com.example.supervisionapp.persistence;

import com.example.supervisionapp.data.model.InvoiceStateModel;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisionRequestTypeModel;
import com.example.supervisionapp.data.model.SupervisoryStateModel;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.data.model.SupervisionRequestModel;
import com.example.supervisionapp.data.model.ThesisStateModel;
import com.example.supervisionapp.data.model.Tuple4;
import com.example.supervisionapp.data.model.UserThesisModel;
import com.example.supervisionapp.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeSource;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function;
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
                        ThesisState thesisState = thesisStateDao.getByState(ThesisStateModel.ADVERTISED.name()).blockingGet();
                        Thesis thesis = new Thesis();
                        thesis.title = title;
                        thesis.description = description;
                        thesis.state = thesisState.id;
                        thesis.id = thesisDao.insert(thesis).blockingGet();

                        SupervisoryType firstSupervisorType = supervisoryTypeDao.getByType(SupervisoryTypeModel.FIRST_SUPERVISOR.name()).blockingGet();
                        InvoiceState unfinishedInvoiceState = invoiceStateDao.getByState(InvoiceStateModel.UNFINISHED.name()).blockingGet();
                        SupervisoryState draftSupervisoryState = supervisoryStateDao.getByState(SupervisoryStateModel.DRAFT.name()).blockingGet();

                        Supervisor supervisor = new Supervisor();
                        supervisor.user = loggedInUser.getUserId();
                        supervisor.thesis = thesis.id;
                        supervisor.state = draftSupervisoryState.id;
                        supervisor.type = firstSupervisorType.id;
                        supervisor.invoiceState = unfinishedInvoiceState.id;
                        supervisorDao.insert(supervisor).blockingAwait();
                    }
                });
            }
        });
    }

    public Completable deleteThesisSupervisorDraft(long thesis) {
        ThesisDao thesisDao = appDatabase.thesisDao();
        SupervisorDao supervisorDao = appDatabase.supervisorDao();
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                appDatabase.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        supervisorDao.deleteByThesisId(thesis).blockingAwait();
                        thesisDao.deleteById(thesis).blockingAwait();
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
                .getByState(SupervisoryStateModel.DRAFT.name())
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
                                thesisStateDao.getByState(ThesisStateModel.ADVERTISED.name()),
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
                                supervisoryStateDao.getById(pair.getSecond().state),
                                supervisoryTypeDao.getById(pair.getSecond().type),
                                invoiceStateDao.getById(pair.getSecond().invoiceState),
                                student.toMaybe(),
                                secondSupervisor.toMaybe(),
                                new Function8<Thesis, Supervisor, ThesisState, SupervisoryState, SupervisoryType, InvoiceState, User, Supervisor, ThesisModel>() {
                                    @Override
                                    public ThesisModel apply(Thesis thesis, Supervisor supervisor, ThesisState thesisState, SupervisoryState supervisoryState, SupervisoryType supervisoryType, InvoiceState invoiceState, User user, Supervisor secondSupervisor) throws Throwable {
                                        String studentName = "";
                                        if (user.id >= 0) {
                                            studentName = Utils.createStudentName(user.foreName, user.name);
                                        }

                                        User defaultFirstSupervisorUser = new User();
                                        defaultFirstSupervisorUser.foreName = "";
                                        defaultFirstSupervisorUser.name = "";
                                        User firstSupervisorUser = userDao.getById(supervisor.user).defaultIfEmpty(defaultFirstSupervisorUser).blockingGet();

                                        User defaultSecondSupervisorUser = new User();
                                        defaultSecondSupervisorUser.foreName = "";
                                        defaultSecondSupervisorUser.name = "";
                                        User secondSupervisorUser = userDao.getById(secondSupervisor.user).defaultIfEmpty(defaultSecondSupervisorUser).blockingGet();

                                        String firstSupervisorName;
                                        String secondSupervisorName;
                                        if (SupervisoryTypeModel.valueOf(supervisoryType.type) == SupervisoryTypeModel.FIRST_SUPERVISOR) {
                                            firstSupervisorName = Utils.createSupervisorName(firstSupervisorUser);;
                                            secondSupervisorName = Utils.createSupervisorName(secondSupervisorUser);
                                        } else {
                                            firstSupervisorName = Utils.createSupervisorName(secondSupervisorUser);
                                            secondSupervisorName = Utils.createSupervisorName(firstSupervisorUser);
                                        }

                                        boolean hasSecondSupervisor = secondSupervisor.user >= 0 && secondSupervisor.thesis >= 0;
                                        return new ThesisModel(
                                                thesis.id,
                                                thesis.title,
                                                thesis.subtitle,
                                                SupervisoryStateModel.valueOf(supervisoryState.state),
                                                SupervisoryTypeModel.valueOf(supervisoryType.type),
                                                studentName,
                                                firstSupervisorName,
                                                secondSupervisorName,
                                                thesis.expose,
                                                ThesisStateModel.valueOf(thesisState.state),
                                                hasSecondSupervisor,
                                                InvoiceStateModel.valueOf(invoiceState.state));
                                    }
                                });
                    }
                });
    }

    public Maybe<ThesisModel> getStudentThesis(LoggedInUser user) {
        StudentDao studentDao = appDatabase.studentDao();
        ThesisDao thesisDao = appDatabase.thesisDao();
        ThesisStateDao thesisStateDao = appDatabase.thesisStateDao();
        SupervisorDao supervisorDao = appDatabase.supervisorDao();
        UserDao userDao = appDatabase.userDao();
        SupervisoryStateDao supervisoryStateDao = appDatabase.supervisoryStateDao();
        SupervisoryTypeDao supervisoryTypeDao = appDatabase.supervisoryTypeDao();
        InvoiceStateDao invoiceStateDao = appDatabase.invoiceStateDao();
        return studentDao
                .getByUser(user.getUserId())
                .flatMap(new Function<List<Student>, MaybeSource<Thesis>>() {
                    @Override
                    public MaybeSource<Thesis> apply(List<Student> students) throws Throwable {
                        List<Long> ids = new ArrayList<>();
                        for (Student student : students) {
                            ids.add(student.thesis);
                        }

                        List<ThesisState> states = thesisStateDao
                                .getByStates(Arrays
                                        .asList(
                                                ThesisStateModel.IN_PROGRESS.name(),
                                                ThesisStateModel.TURNED_IN.name(),
                                                ThesisStateModel.RATED.name()
                                        )
                                ).blockingGet();

                        List<Long> thesisStateIds = new ArrayList<>(states.size());
                        for (ThesisState thesisState : states) {
                            thesisStateIds.add(thesisState.id);
                        }

                        List<Thesis> theses = thesisDao
                                .getByIdsAndStates(ids, thesisStateIds).blockingGet();
                        if (theses.isEmpty()) {
                            return Maybe.empty();
                        }
                        if (theses.size() > 1) {
                            throw new IllegalStateException("More than one thesis in progress for user");
                        }
                        return Maybe.just(theses.get(0));
                    }
                }).flatMap(new Function<Thesis, MaybeSource<Pair<Thesis, List<Supervisor>>>>() {
                    @Override
                    public MaybeSource<Pair<Thesis, List<Supervisor>>> apply(Thesis thesis) throws Throwable {
                        return Maybe.zip(Maybe.just(thesis),
                                supervisorDao.getByThesis(thesis.id),
                                new BiFunction<Thesis, List<Supervisor>, Pair<Thesis, List<Supervisor>>>() {
                                    @Override
                                    public Pair<Thesis, List<Supervisor>> apply(Thesis thesis, List<Supervisor> supervisors) throws Throwable {
                                        return new Pair<>(thesis, supervisors);
                                    }
                                });
                    }
                })
                .flatMap(new Function<Pair<Thesis, List<Supervisor>>, MaybeSource<ThesisModel>>() {
                    @Override
                    public MaybeSource<ThesisModel> apply(Pair<Thesis, List<Supervisor>> pair) throws Throwable {
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
                        SupervisoryType firstSupervisorType = supervisoryTypeDao.getByType(SupervisoryTypeModel.FIRST_SUPERVISOR.name()).blockingGet();
                        List<Supervisor> supervisors = pair.getSecond();
                        Supervisor firstSupervisor;
                        Supervisor secondSupervisor;
                        if (supervisors.size() == 1) {
                            firstSupervisor = supervisors.get(0);
                            secondSupervisor = new Supervisor();
                            secondSupervisor.thesis = -1;
                            secondSupervisor.user = -1;
                        } else if (supervisors.size() == 2) {
                            if (supervisors.get(0).type == firstSupervisorType.id) {
                                firstSupervisor = supervisors.get(0);
                                secondSupervisor = supervisors.get(1);
                            } else {
                                firstSupervisor = supervisors.get(1);
                                secondSupervisor = supervisors.get(0);
                            }
                        } else {
                            throw new IllegalStateException("More than two supervisors for thesis");
                        }
                        return Maybe.zip(Maybe.just(pair.getFirst()),
                                Maybe.just(firstSupervisor),
                                thesisStateDao.getById(pair.getFirst().state).toMaybe(),
                                supervisoryStateDao.getById(firstSupervisor.state),
                                supervisoryTypeDao.getById(firstSupervisor.type),
                                invoiceStateDao.getById(firstSupervisor.invoiceState),
                                student.toMaybe(),
                                Maybe.just(secondSupervisor),
                                new Function8<Thesis, Supervisor, ThesisState, SupervisoryState, SupervisoryType, InvoiceState, User, Supervisor, ThesisModel>() {
                                    @Override
                                    public ThesisModel apply(Thesis thesis, Supervisor supervisor, ThesisState thesisState, SupervisoryState supervisoryState, SupervisoryType supervisoryType, InvoiceState invoiceState, User user, Supervisor secondSupervisor) throws Throwable {
                                        String studentName = "";
                                        if (user.id >= 0) {
                                            studentName = Utils.createStudentName(user);
                                        }

                                        User defaultFirstSupervisorUser = new User();
                                        defaultFirstSupervisorUser.foreName = "";
                                        defaultFirstSupervisorUser.name = "";
                                        User firstSupervisorUser = userDao.getById(supervisor.user).defaultIfEmpty(defaultFirstSupervisorUser).blockingGet();

                                        User defaultSecondSupervisorUser = new User();
                                        defaultSecondSupervisorUser.foreName = "";
                                        defaultSecondSupervisorUser.name = "";
                                        User secondSupervisorUser = userDao.getById(secondSupervisor.user).defaultIfEmpty(defaultSecondSupervisorUser).blockingGet();

                                        String firstSupervisorName;
                                        String secondSupervisorName;
                                        if (SupervisoryTypeModel.valueOf(supervisoryType.type) == SupervisoryTypeModel.FIRST_SUPERVISOR) {
                                            firstSupervisorName = Utils.createSupervisorName(firstSupervisorUser);
                                            secondSupervisorName = Utils.createSupervisorName(secondSupervisorUser);
                                        } else {
                                            firstSupervisorName = Utils.createSupervisorName(secondSupervisorUser);
                                            secondSupervisorName = Utils.createSupervisorName(firstSupervisorUser);
                                        }

                                        boolean hasSecondSupervisor = secondSupervisor.user >= 0 && secondSupervisor.thesis >= 0;
                                        return new ThesisModel(
                                                thesis.id,
                                                thesis.title,
                                                thesis.subtitle,
                                                SupervisoryStateModel.valueOf(supervisoryState.state),
                                                SupervisoryTypeModel.valueOf(supervisoryType.type),
                                                studentName,
                                                firstSupervisorName,
                                                secondSupervisorName,
                                                thesis.expose,
                                                ThesisStateModel.valueOf(thesisState.state),
                                                hasSecondSupervisor,
                                                InvoiceStateModel.valueOf(invoiceState.state));
                                    }
                                });
                    }
                });
    }

    public Maybe<List<ThesisModel>> getSupervisorsTheses(LoggedInUser loggedInUser, SupervisoryStatePredicate predicate) {
        SupervisorDao supervisorDao = appDatabase.supervisorDao();
        ThesisDao thesisDao = appDatabase.thesisDao();
        ThesisStateDao thesisStateDao = appDatabase.thesisStateDao();
        SupervisoryStateDao supervisoryStateDao = appDatabase.supervisoryStateDao();
        SupervisoryTypeDao supervisoryTypeDao = appDatabase.supervisoryTypeDao();
        InvoiceStateDao invoiceStateDao = appDatabase.invoiceStateDao();
        StudentDao studentDao = appDatabase.studentDao();
        UserDao userDao = appDatabase.userDao();
        return supervisorDao
                .getByUser(loggedInUser.getUserId())
                .map(new Function<List<Supervisor>, List<Tuple4<Supervisor, SupervisoryState, SupervisoryType, InvoiceState>>>() {
                    @Override
                    public List<Tuple4<Supervisor, SupervisoryState, SupervisoryType, InvoiceState>> apply(List<Supervisor> supervisors) throws Throwable {
                        List<Tuple4<Supervisor, SupervisoryState, SupervisoryType, InvoiceState>> tuples = new ArrayList<>(supervisors.size());
                        for (Supervisor supervisor : supervisors) {
                            SupervisoryState supervisoryState = supervisoryStateDao.getById(supervisor.state).blockingGet();
                            SupervisoryStateModel supervisoryStateModel = SupervisoryStateModel.valueOf(supervisoryState.state);
                            if (predicate.test(supervisoryStateModel)) {
                                SupervisoryType supervisoryType = supervisoryTypeDao.getById(supervisor.type).blockingGet();
                                InvoiceState invoiceState = invoiceStateDao.getById(supervisor.invoiceState).blockingGet();
                                tuples.add(new Tuple4<>(supervisor, supervisoryState, supervisoryType, invoiceState));
                            }
                        }
                        return tuples;
                    }
                })
                .map(new Function<List<Tuple4<Supervisor, SupervisoryState, SupervisoryType, InvoiceState>>, List<ThesisModel>>() {
                    @Override
                    public List<ThesisModel> apply(List<Tuple4<Supervisor, SupervisoryState, SupervisoryType, InvoiceState>> tuples) throws Throwable {
                        List<ThesisModel> theses = new ArrayList<>(tuples.size());
                        for (Tuple4<Supervisor, SupervisoryState, SupervisoryType, InvoiceState> tuple : tuples) {
                            Supervisor supervisor = tuple.getFirst();
                            SupervisoryState supervisoryState = tuple.getSecond();
                            SupervisoryType supervisoryType = tuple.getThird();
                            InvoiceState invoiceState = tuple.getFourth();

                            Thesis thesis = thesisDao.getById(supervisor.thesis).blockingGet();
                            ThesisState thesisState = thesisStateDao.getById(thesis.state).blockingGet();

                            User defaultUser = new User();
                            defaultUser.id = -1;
                            User student = studentDao
                                    .getByThesis(thesis.id)
                                    .flatMap(new Function<Student, MaybeSource<User>>() {
                                        @Override
                                        public MaybeSource<User> apply(Student student) throws Throwable {
                                            return userDao.getById(student.user);
                                        }
                                    })
                                    .defaultIfEmpty(defaultUser)
                                    .blockingGet();

                            Supervisor defaultSupervisor = new Supervisor();
                            defaultSupervisor.user = -1;
                            defaultSupervisor.thesis = -1;
                            Supervisor secondSupervisor = supervisorDao
                                    .getByThesisWhereUserIsNot(thesis.id, loggedInUser.getUserId())
                                    .defaultIfEmpty(defaultSupervisor)
                                    .blockingGet();

                            User defaultFirstSupervisorUser = new User();
                            defaultFirstSupervisorUser.foreName = "";
                            defaultFirstSupervisorUser.name = "";
                            User firstSupervisorUser = userDao.getById(supervisor.user).defaultIfEmpty(defaultFirstSupervisorUser).blockingGet();

                            User defaultSecondSupervisorUser = new User();
                            defaultSecondSupervisorUser.foreName = "";
                            defaultSecondSupervisorUser.name = "";
                            User secondSupervisorUser = userDao.getById(secondSupervisor.user).defaultIfEmpty(defaultSecondSupervisorUser).blockingGet();

                            boolean hasSecondSupervisor = secondSupervisor.user >= 0 && secondSupervisor.thesis >= 0;

                            String firstSupervisorName;
                            String secondSupervisorName;
                            if (SupervisoryTypeModel.valueOf(supervisoryType.type) == SupervisoryTypeModel.FIRST_SUPERVISOR) {
                                firstSupervisorName = Utils.createSupervisorName(firstSupervisorUser);
                                secondSupervisorName = Utils.createSupervisorName(secondSupervisorUser);
                            } else {
                                firstSupervisorName = Utils.createSupervisorName(secondSupervisorUser);
                                secondSupervisorName = Utils.createSupervisorName(firstSupervisorUser);
                            }

                            theses.add(new ThesisModel(thesis.id,
                                    thesis.title,
                                    thesis.subtitle,
                                    SupervisoryStateModel.valueOf(supervisoryState.state),
                                    SupervisoryTypeModel.valueOf(supervisoryType.type),
                                    Utils.createStudentName(student),
                                    firstSupervisorName,
                                    secondSupervisorName,
                                    thesis.expose,
                                    ThesisStateModel.valueOf(thesisState.state),
                                    hasSecondSupervisor,
                                    InvoiceStateModel.valueOf(invoiceState.state)));
                        }
                        return theses;
                    }
                });
    }

    public Maybe<List<ThesisModel>> getSupervisorsSupervisedTheses(LoggedInUser loggedInUser) {
        return getSupervisorsTheses(loggedInUser, new SupervisoryStatePredicate() {
            @Override
            public boolean test(SupervisoryStateModel supervisoryStateModel) {
                return supervisoryStateModel == SupervisoryStateModel.SUPERVISED;
            }
        });
    }

    public Maybe<List<SupervisionRequestModel>> getSupervisionRequestsForUser(LoggedInUser loggedInUser) {
        SupervisionRequestDao supervisionRequestDao = appDatabase.supervisionRequestDao();
        SupervisionRequestTypeDao supervisionRequestTypeDao = appDatabase.supervisionRequestTypeDao();
        SupervisorDao supervisorDao = appDatabase.supervisorDao();
        ThesisDao thesisDao = appDatabase.thesisDao();
        UserDao userDao = appDatabase.userDao();
        SupervisoryTypeDao supervisoryTypeDao = appDatabase.supervisoryTypeDao();
        StudentDao studentDao = appDatabase.studentDao();
        return supervisorDao
                .getByUser(loggedInUser.getUserId())
                .flatMap(new Function<List<Supervisor>, MaybeSource<List<Pair<Supervisor, SupervisionRequest>>>>() {
                    @Override
                    public MaybeSource<List<Pair<Supervisor, SupervisionRequest>>> apply(List<Supervisor> supervisors) throws Throwable {
                        List<Long> thesisIds = new ArrayList<>(supervisors.size());
                        for (Supervisor supervisor : supervisors) {
                            thesisIds.add(supervisor.thesis);
                        }
                        return Maybe.zip(
                                Maybe.just(supervisors),
                                supervisionRequestDao.getByTheses(thesisIds),
                                new BiFunction<List<Supervisor>, List<SupervisionRequest>, List<Pair<Supervisor, SupervisionRequest>>>() {
                                    @Override
                                    public List<Pair<Supervisor, SupervisionRequest>> apply(List<Supervisor> supervisors, List<SupervisionRequest> supervisionRequests) throws Throwable {
                                        List<Pair<Supervisor, SupervisionRequest>> result = new ArrayList<>();
                                        SupervisionRequestType secondSupervisionRequestType = supervisionRequestTypeDao
                                                .getByType(SupervisionRequestTypeModel.SECOND_SUPERVISOR.name())
                                                .blockingGet();
                                        // supervisors should be filtered here
                                        // resulting list should only contain
                                        // entities where a request was supplied for
                                        for (Supervisor supervisor : supervisors) {
                                            for (SupervisionRequest supervisionRequest : supervisionRequests) {
                                                if (supervisionRequest.thesis == supervisor.thesis) {
                                                    if (supervisionRequest.type == secondSupervisionRequestType.id &&
                                                            supervisionRequest.user != loggedInUser.getUserId()) {
                                                        continue;
                                                    }
                                                    result.add(new Pair<>(supervisor, supervisionRequest));
                                                }
                                            }
                                        }
                                        return result;
                                    }
                                }
                        );
                    }
                })
                .map(new Function<List<Pair<Supervisor, SupervisionRequest>>, List<SupervisionRequestModel>>() {
                    @Override
                    public List<SupervisionRequestModel> apply(List<Pair<Supervisor, SupervisionRequest>> pairs) throws Throwable {
                        List<SupervisionRequestModel> results = new ArrayList<>(pairs.size());
                        SupervisoryType firstSupervisorType = supervisoryTypeDao
                                .getByType(SupervisoryTypeModel.FIRST_SUPERVISOR.name())
                                .blockingGet();
                        for (Pair<Supervisor, SupervisionRequest> request : pairs) {
                            Supervisor supervisor = request.getFirst();
                            SupervisionRequest supervisionRequest = request.getSecond();
                            Thesis thesis = thesisDao.getById(supervisor.thesis).blockingGet();
                            SupervisionRequestType supervisionRequestType = supervisionRequestTypeDao.getById(supervisionRequest.type).blockingGet();
                            SupervisionRequestTypeModel requestType = SupervisionRequestTypeModel.valueOf(supervisionRequestType.type);
                            User requestingUser = userDao.getById(supervisionRequest.user).blockingGet();
                            String studentName = Utils.createStudentName(requestingUser);
                            String supervisorName;
                            String subTitle;
                            String description;
                            String expose;
                            if (requestType == SupervisionRequestTypeModel.SUPERVISION) {
                                supervisorName = null;
                                subTitle = supervisionRequest.subtitle;
                                description = supervisionRequest.description;
                                expose = supervisionRequest.expose;
                            } else {
                                Supervisor thesisSupervisor = supervisorDao
                                        .getByThesisAndType(thesis.id, firstSupervisorType.id)
                                        .blockingGet();
                                User supervisorUser = userDao
                                        .getById(thesisSupervisor.user)
                                        .blockingGet();
                                supervisorName = Utils.createSupervisorName(supervisorUser);
                                subTitle = thesis.subtitle;
                                description = thesis.description;
                                expose = thesis.expose;
                            }

                            results.add(new SupervisionRequestModel(
                                    thesis.id,
                                    supervisionRequest.user,
                                    thesis.title,
                                    subTitle,
                                    studentName,
                                    supervisorName,
                                    requestType,
                                    description,
                                    expose));
                        }
                        List<SupervisionRequest> additionalSecondarySupervisionRequests = supervisionRequestDao
                                .getByUserId(loggedInUser.getUserId())
                                .blockingGet();
                        for (SupervisionRequest supervisionRequest : additionalSecondarySupervisionRequests) {
                            Thesis thesis = thesisDao.getById(supervisionRequest.thesis).blockingGet();
                            Supervisor firstSupervisor = supervisorDao
                                    .getByThesisAndType(supervisionRequest.thesis, firstSupervisorType.id)
                                    .blockingGet();
                            User firstSupervisorUser = userDao
                                    .getById(firstSupervisor.user)
                                    .blockingGet();
                            String supervisorName = Utils.createSupervisorName(firstSupervisorUser);
                            Student student = studentDao
                                    .getByThesis(supervisionRequest.thesis)
                                    .blockingGet();
                            User studentUser = userDao
                                    .getById(student.user)
                                    .blockingGet();
                            String studentName = Utils.createStudentName(studentUser);
                            results.add(new SupervisionRequestModel(
                                    thesis.id,
                                    supervisionRequest.user,
                                    thesis.title,
                                    thesis.subtitle,
                                    studentName,
                                    supervisorName,
                                    SupervisionRequestTypeModel.SECOND_SUPERVISOR,
                                    thesis.description,
                                    thesis.expose));
                        }
                        return results;
                    }
                });
    }

    public Completable requestSecondSupervisor(long thesisId, long userId) {
        SupervisionRequestDao supervisionRequestDao = appDatabase.supervisionRequestDao();
        SupervisionRequestTypeDao supervisionRequestTypeDao = appDatabase.supervisionRequestTypeDao();
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                SupervisionRequestType secondSupervisionRequestType = supervisionRequestTypeDao
                        .getByType(SupervisionRequestTypeModel.SECOND_SUPERVISOR.name())
                        .blockingGet();

                SupervisionRequest supervisionRequest = new SupervisionRequest();
                supervisionRequest.thesis = thesisId;
                supervisionRequest.user = userId;
                supervisionRequest.type = secondSupervisionRequestType.id;
                supervisionRequestDao.insert(supervisionRequest).blockingAwait();
            }
        });
    }

    public Maybe<List<UserThesisModel>> getAdvertisedTheses(LoggedInUser loggedInUser) {
        ThesisStateDao thesisStateDao = appDatabase.thesisStateDao();
        ThesisDao thesisDao = appDatabase.thesisDao();
        SupervisionRequestDao supervisionRequestDao = appDatabase.supervisionRequestDao();
        return thesisStateDao
                .getByState(ThesisStateModel.ADVERTISED.name())
                .flatMap(new Function<ThesisState, MaybeSource<List<Thesis>>>() {
                    @Override
                    public MaybeSource<List<Thesis>> apply(ThesisState thesisState) throws Throwable {
                        return thesisDao
                                .getByState(thesisState.id);
                    }
                })
                .map(new Function<List<Thesis>, List<UserThesisModel>>() {
                    @Override
                    public List<UserThesisModel> apply(List<Thesis> theses) throws Throwable {
                        List<UserThesisModel> userTheses = new ArrayList<>(theses.size());
                        for (Thesis thesis : theses) {
                            SupervisionRequest existingRequest = supervisionRequestDao
                                    .getByThesisIdAndUserId(thesis.id, loggedInUser.getUserId())
                                    .blockingGet();
                            boolean alreadyRequested = existingRequest != null;
                            userTheses.add(new UserThesisModel(
                                    thesis.id,
                                    thesis.title,
                                    thesis.subtitle,
                                    thesis.description,
                                    thesis.expose,
                                    thesis.state,
                                    alreadyRequested
                            ));
                        }
                        return userTheses;
                    }
                });
    }

    public Maybe<Thesis> getThesisById(long id) {
        ThesisDao thesisDao = appDatabase.thesisDao();
        return thesisDao
                .getById(id);
    }

    public Maybe<SupervisionRequestModel> getSupervisionRequestByThesisAndUser(long thesisId, long userId) {
        SupervisionRequestDao supervisionRequestDao = appDatabase.supervisionRequestDao();
        SupervisorDao supervisorDao = appDatabase.supervisorDao();
        SupervisoryTypeDao supervisoryTypeDao = appDatabase.supervisoryTypeDao();
        ThesisDao thesisDao = appDatabase.thesisDao();
        UserDao userDao = appDatabase.userDao();
        SupervisionRequestTypeDao supervisionRequestTypeDao = appDatabase.supervisionRequestTypeDao();
        return supervisionRequestDao
                .getByThesisIdAndUserId(thesisId, userId)
                .flatMap(new Function<SupervisionRequest, MaybeSource<Pair<Supervisor, SupervisionRequest>>>() {
                    @Override
                    public MaybeSource<Pair<Supervisor, SupervisionRequest>> apply(SupervisionRequest supervisionRequest) throws Throwable {
                        SupervisoryType supervisoryType = supervisoryTypeDao
                                .getByType(SupervisoryTypeModel.FIRST_SUPERVISOR.name())
                                .blockingGet();
                        return Maybe.zip(
                                supervisorDao.getByThesisAndType(thesisId, supervisoryType.id),
                                Maybe.just(supervisionRequest),
                                new BiFunction<Supervisor, SupervisionRequest, Pair<Supervisor, SupervisionRequest>>() {
                                    @Override
                                    public Pair<Supervisor, SupervisionRequest> apply(Supervisor supervisor, SupervisionRequest supervisionRequest) throws Throwable {
                                        return new Pair<>(supervisor, supervisionRequest);
                                    }
                                }
                        );
                    }
                })
                .map(new Function<Pair<Supervisor, SupervisionRequest>, SupervisionRequestModel>() {
                    @Override
                    public SupervisionRequestModel apply(Pair<Supervisor, SupervisionRequest> pair) throws Throwable {
                        Supervisor supervisor = pair.getFirst();
                        SupervisionRequest supervisionRequest = pair.getSecond();
                        Thesis thesis = thesisDao.getById(supervisor.thesis).blockingGet();
                        SupervisionRequestType supervisionRequestType = supervisionRequestTypeDao.getById(supervisionRequest.type).blockingGet();
                        SupervisionRequestTypeModel requestType = SupervisionRequestTypeModel.valueOf(supervisionRequestType.type);
                        User requestingUser = userDao.getById(supervisionRequest.user).blockingGet();
                        String studentName = Utils.createStudentName(requestingUser);
                        String supervisorName;
                        String subTitle;
                        String description;
                        String expose;
                        if (requestType == SupervisionRequestTypeModel.SUPERVISION) {
                            supervisorName = null;
                            subTitle = supervisionRequest.subtitle;
                            description = supervisionRequest.description;
                            expose = supervisionRequest.expose;
                        } else {
                            SupervisoryType firstSupervisorType = supervisoryTypeDao
                                    .getByType(SupervisoryTypeModel.FIRST_SUPERVISOR.name())
                                    .blockingGet();
                            Supervisor thesisSupervisor = supervisorDao
                                    .getByThesisAndType(thesis.id, firstSupervisorType.id)
                                    .blockingGet();
                            User supervisorUser = userDao
                                    .getById(thesisSupervisor.user)
                                    .blockingGet();
                            supervisorName = Utils.createSupervisorName(supervisorUser);
                            subTitle = thesis.subtitle;
                            description = thesis.description;
                            expose = thesis.expose;
                        }

                        return new SupervisionRequestModel(
                                thesis.id,
                                requestingUser.id,
                                thesis.title,
                                subTitle,
                                studentName,
                                supervisorName,
                                SupervisionRequestTypeModel.valueOf(supervisionRequestType.type),
                                description,
                                expose
                        );
                    }
                });
    }

    public Completable acceptSupervisionRequest(SupervisionRequestModel supervisionRequest) {
        SupervisionRequestDao supervisionRequestDao = appDatabase.supervisionRequestDao();
        SupervisorDao supervisorDao = appDatabase.supervisorDao();
        SupervisoryTypeDao supervisoryTypeDao = appDatabase.supervisoryTypeDao();
        SupervisoryStateDao supervisoryStateDao = appDatabase.supervisoryStateDao();
        SupervisionRequestTypeDao supervisionRequestTypeDao = appDatabase.supervisionRequestTypeDao();
        StudentDao studentDao = appDatabase.studentDao();
        InvoiceStateDao invoiceStateDao = appDatabase.invoiceStateDao();
        ThesisDao thesisDao = appDatabase.thesisDao();
        ThesisStateDao thesisStateDao = appDatabase.thesisStateDao();
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                appDatabase.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        SupervisoryState supervisedSupervisoryState = supervisoryStateDao
                                .getByState(SupervisoryStateModel.SUPERVISED.name())
                                .blockingGet();
                        if (supervisionRequest.getRequestType() == SupervisionRequestTypeModel.SUPERVISION) {
                            SupervisoryType firstSupervisorType = supervisoryTypeDao
                                    .getByType(SupervisoryTypeModel.FIRST_SUPERVISOR.name())
                                    .blockingGet();
                            SupervisoryState draftState = supervisoryStateDao
                                    .getByState(SupervisoryStateModel.DRAFT.name())
                                    .blockingGet();
                            Supervisor firstSupervisor = supervisorDao
                                    .getByThesisAndType(
                                            supervisionRequest.getThesisId(),
                                            firstSupervisorType.id)
                                    .blockingGet();
                            if (firstSupervisor.state != draftState.id) {
                                throw new IllegalStateException("Thesis is not in draft state anymore and seems to be supervised by now");
                            }
                            firstSupervisor.state = supervisedSupervisoryState.id;
                            supervisorDao
                                    .update(firstSupervisor)
                                    .blockingAwait();

                            ThesisState inProgressState = thesisStateDao
                                    .getByState(ThesisStateModel.IN_PROGRESS.name())
                                    .blockingGet();

                            Thesis thesis = thesisDao
                                    .getById(supervisionRequest.getThesisId())
                                    .blockingGet();
                            thesis.subtitle = supervisionRequest.getSubTitle();
                            thesis.description = supervisionRequest.getDescription();
                            thesis.expose = supervisionRequest.getExpose();
                            thesis.state = inProgressState.id;
                            thesisDao.update(thesis).blockingAwait();

                            Student student = new Student();
                            student.thesis = supervisionRequest.getThesisId();
                            student.user = supervisionRequest.getRequestingUserId();
                            studentDao.insert(student).blockingAwait();
                        } else if (supervisionRequest.getRequestType() == SupervisionRequestTypeModel.SECOND_SUPERVISOR) {
                            SupervisoryType secondSupervisorSupervisoryType = supervisoryTypeDao
                                    .getByType(SupervisoryTypeModel.SECOND_SUPERVISOR.name())
                                    .blockingGet();
                            InvoiceState unfinishedInvoiceState = invoiceStateDao
                                    .getByState(InvoiceStateModel.UNFINISHED.name())
                                    .blockingGet();
                            Supervisor supervisor = new Supervisor();
                            supervisor.thesis = supervisionRequest.getThesisId();
                            supervisor.user = supervisionRequest.getRequestingUserId();
                            supervisor.state = supervisedSupervisoryState.id;
                            supervisor.type = secondSupervisorSupervisoryType.id;
                            supervisor.invoiceState = unfinishedInvoiceState.id;
                            supervisorDao.insert(supervisor).blockingAwait();
                        }
                        SupervisionRequestType supervisionRequestType = supervisionRequestTypeDao
                                .getByType(supervisionRequest.getRequestType().name())
                                .blockingGet();
                        supervisionRequestDao
                                .deleteByThesisIdAndType(
                                        supervisionRequest.getThesisId(),
                                        supervisionRequestType.id
                                )
                                .blockingAwait();
                    }
                });
            }
        });
    }

    public Completable rejectSupervisionRequest(SupervisionRequestModel supervisionRequest) {
        SupervisionRequestDao supervisionRequestDao = appDatabase.supervisionRequestDao();
        return Completable
                .fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (supervisionRequest == null) {
                            return;
                        }
                        appDatabase
                                .runInTransaction(new Runnable() {
                                    @Override
                                    public void run() {
                                        SupervisionRequest request = supervisionRequestDao
                                                .getByThesisIdAndUserId(
                                                        supervisionRequest.getThesisId(),
                                                        supervisionRequest.getRequestingUserId())
                                                .blockingGet();
                                        supervisionRequestDao
                                                .delete(request)
                                                .blockingAwait();
                                    }
                                });
                    }
                });
    }

    public Completable requestSupervision(long thesisId,
                                          LoggedInUser student,
                                          String subTitle,
                                          String description,
                                          String expose) {
        SupervisionRequestTypeDao supervisionRequestTypeDao = appDatabase.supervisionRequestTypeDao();
        SupervisionRequestDao supervisionRequestDao = appDatabase.supervisionRequestDao();
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                appDatabase
                        .runInTransaction(new Runnable() {
                            @Override
                            public void run() {
                                SupervisionRequestType supervisionRequestType = supervisionRequestTypeDao
                                        .getByType(SupervisionRequestTypeModel.SUPERVISION.name())
                                        .blockingGet();
                                SupervisionRequest supervisionRequest = new SupervisionRequest();
                                supervisionRequest.thesis = thesisId;
                                supervisionRequest.user = student.getUserId();
                                supervisionRequest.subtitle = subTitle;
                                supervisionRequest.description = description;
                                supervisionRequest.expose = expose;
                                supervisionRequest.type = supervisionRequestType.id;
                                supervisionRequestDao.insert(supervisionRequest).blockingAwait();
                            }
                        });
            }
        });
    }

    // TODO: test
    public Completable updateThesis(
            long thesisId,
            LoggedInUser loggedInUser,
            ThesisStateModel thesisState,
            InvoiceStateModel invoiceState) {
        ThesisDao thesisDao = appDatabase.thesisDao();
        SupervisorDao supervisorDao = appDatabase.supervisorDao();
        SupervisoryStateDao supervisoryStateDao = appDatabase.supervisoryStateDao();
        ThesisStateDao thesisStateDao = appDatabase.thesisStateDao();
        InvoiceStateDao invoiceStateDao = appDatabase.invoiceStateDao();
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                Thesis thesis = thesisDao.getById(thesisId).blockingGet();
                if (thesisState != null) {
                    ThesisState databaseThesisState = thesisStateDao
                            .getByState(thesisState.name())
                            .blockingGet();
                    thesis.state = databaseThesisState.id;
                    thesisDao.update(thesis).blockingAwait();
                }
                if (invoiceState != null) {
                    InvoiceState databaseInvoiceState = invoiceStateDao
                            .getByState(invoiceState.name())
                            .blockingGet();
                    Supervisor supervisor = supervisorDao
                            .getByUserAndThesis(loggedInUser.getUserId(), thesisId)
                            .blockingGet();
                    supervisor.invoiceState = databaseInvoiceState.id;
                    supervisorDao.update(supervisor).blockingAwait();
                }
                thesisDao.update(thesis);
            }
        });
    }

    // TODO: tests
    public Maybe<Boolean> studentHasThesis(LoggedInUser user) {
        StudentDao studentDao = appDatabase.studentDao();
        ThesisDao thesisDao = appDatabase.thesisDao();
        ThesisStateDao thesisStateDao = appDatabase.thesisStateDao();
        return studentDao
                .getByUser(user.getUserId())
                .map(new Function<List<Student>, Boolean>() {
                    @Override
                    public Boolean apply(List<Student> students) throws Throwable {
                        for (Student student : students) {
                            Thesis thesis = thesisDao
                                    .getById(student.thesis)
                                    .blockingGet();
                            ThesisState thesisState = thesisStateDao
                                    .getById(thesis.state)
                                    .blockingGet();
                            ThesisStateModel thesisStateModel = ThesisStateModel.valueOf(thesisState.state);
                            if (thesisStateModel.getSortPosition() < ThesisStateModel.FINISHED.getSortPosition()) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }
}

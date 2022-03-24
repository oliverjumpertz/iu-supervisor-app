package com.example.supervisionapp.persistence;

import android.util.Pair;

import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.User;
import com.example.supervisionapp.data.model.UserTypeModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeSource;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function;

public class UserRepository {
    private final AppDatabase appDatabase;

    public UserRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public Maybe<User> getUserByUsername(String username) {
        UserDao userDao = appDatabase.userDao();
        UserTypeDao userTypeDao = appDatabase.userTypeDao();
        return userDao
                .getByUsername(username)
                .flatMap(new Function<com.example.supervisionapp.persistence.User, MaybeSource<Pair<com.example.supervisionapp.persistence.User, UserType>>>() {
                    @Override
                    public MaybeSource<Pair<com.example.supervisionapp.persistence.User, UserType>> apply(com.example.supervisionapp.persistence.User user) throws Throwable {
                        Maybe<UserType> userType = userTypeDao.getById(user.type);
                        return Maybe.zip(Maybe.just(user), userType, new BiFunction<com.example.supervisionapp.persistence.User, UserType, Pair<com.example.supervisionapp.persistence.User, UserType>>() {
                            @Override
                            public Pair<com.example.supervisionapp.persistence.User, UserType> apply(com.example.supervisionapp.persistence.User user, UserType userType) throws Throwable {
                                return Pair.create(user, userType);
                            }
                        });
                    }
                }).map(new Function<Pair<com.example.supervisionapp.persistence.User, UserType>, User>() {
                    @Override
                    public User apply(Pair<com.example.supervisionapp.persistence.User, UserType> userUserTypePair) throws Throwable {
                        com.example.supervisionapp.persistence.User dbUser = userUserTypePair.first;
                        UserType dbUserType = userUserTypePair.second;
                        return new User(dbUser.id, dbUser.username, dbUser.password, dbUser.title, dbUser.name, dbUser.foreName, UserTypeModel.valueOf(dbUserType.type));
                    }
                });
    }

    public Maybe<List<User>> getSupervisorsExcept(LoggedInUser user) {
        SupervisorDao supervisorDao = appDatabase.supervisorDao();
        UserDao userDao = appDatabase.userDao();
        UserTypeDao userTypeDao = appDatabase.userTypeDao();
        return supervisorDao
                .getWhereUserIsNot(user.getUserId())
                .flatMap(new Function<List<Supervisor>, MaybeSource<List<User>>>() {
                    @Override
                    public MaybeSource<List<User>> apply(List<Supervisor> supervisors) throws Throwable {
                        Set<Long> ids = new HashSet<>();
                        for (Supervisor supervisor : supervisors) {
                            ids.add(supervisor.user);
                        }
                        return userDao.getByIds(new ArrayList<>(ids))
                                .map(new Function<List<com.example.supervisionapp.persistence.User>, List<User>>() {
                                    @Override
                                    public List<User> apply(List<com.example.supervisionapp.persistence.User> users) throws Throwable {
                                        List<User> userModels = new ArrayList<>();
                                        for (com.example.supervisionapp.persistence.User user : users) {
                                            UserType userType = userTypeDao.getById(user.type).blockingGet();
                                            userModels.add(new User(user.id,
                                                    user.username,
                                                    user.password,
                                                    user.title,
                                                    user.name,
                                                    user.foreName,
                                                    UserTypeModel.valueOf(userType.type)));
                                        }
                                        return userModels;
                                    }
                                });
                    }
                });
    }
}

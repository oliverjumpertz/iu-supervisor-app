package com.example.supervisionapp.persistence;

import android.util.Pair;

import com.example.supervisionapp.data.model.User;

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
                        return new User(dbUser.id, dbUser.username, dbUser.password, dbUser.title, dbUser.name, dbUser.foreName, com.example.supervisionapp.data.model.UserType.valueOf(dbUserType.type));
                    }
                });
    }
}

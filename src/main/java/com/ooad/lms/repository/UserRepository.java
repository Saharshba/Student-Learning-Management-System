package com.ooad.lms.repository;

import com.ooad.lms.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    private final InMemoryDataStore dataStore;

    public UserRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(dataStore.users().get(id));
    }

    public User save(User user) {
        dataStore.users().put(user.getUserId(), user);
        dataStore.persist();
        return user;
    }
}
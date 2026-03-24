package ru.job4j.bmb.repository;

import ru.job4j.bmb.model.User;
import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User findByClientId(Long clientId);
}

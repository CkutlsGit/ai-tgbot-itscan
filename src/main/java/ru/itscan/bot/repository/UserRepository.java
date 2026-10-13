package ru.itscan.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itscan.bot.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserById(Long id);
    User getUserById(Long id);
}

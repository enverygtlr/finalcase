package com.definex.finalcase.repository;

import com.definex.finalcase.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailAndDeletedFalse(String email);

    Optional<User> findByIdAndDeletedFalse(UUID id);

    List<User> findAllByDeletedFalse();

    default Optional<User> findActiveUserById(UUID id) {
        return findByIdAndDeletedFalse(id);
    }

    default Optional<User> findActiveUserByEmail(String email) {
        return findByEmailAndDeletedFalse(email);
    }

    default List<User> findActiveUsers() {
        return findAllByDeletedFalse();
    }
}
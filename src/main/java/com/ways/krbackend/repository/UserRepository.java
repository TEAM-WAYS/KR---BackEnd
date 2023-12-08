package com.ways.krbackend.repository;

import com.ways.krbackend.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Manager,Integer> {
    Optional<Manager> findByUserName(String userName);

    boolean existsByUserName(String username);
}

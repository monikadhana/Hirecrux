package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);  //while login wants email and for optional work as if else-- checks the email id exist or not
    boolean existsByEmail(String email); //while register checks mail exist or not
}

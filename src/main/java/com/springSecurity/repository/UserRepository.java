package com.springSecurity.repository;


import com.springSecurity.entities.Role;
import com.springSecurity.entities.UserData;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserData, Long> {
    Optional<UserData> findByEmail(String email);
   @Transactional
    void deleteByEmail(String email);
   UserData findByRole(Role role);
}

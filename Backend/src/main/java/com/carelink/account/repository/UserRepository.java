package com.carelink.account.repository;

import com.carelink.account.model.User;
import com.carelink.account.model.UserCredentials;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    User findByPhoneNumber(String phoneNumber);

    UserCredentials getUserCredentialsByUserID(Integer userID);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") String role);
}
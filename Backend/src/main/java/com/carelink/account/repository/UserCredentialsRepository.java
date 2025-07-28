package com.carelink.account.repository;

import com.carelink.account.model.User;
import com.carelink.account.model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Integer> {
    Optional<UserCredentials> findByUsername(String username);

    UserCredentials findByUser(User user);

    UserCredentials findByResetPasswordToken(String token);

    UserCredentials findByUser_UserID(Integer UserID);
}
package com.carelink.account.repository;

import com.carelink.account.model.User;
import com.carelink.account.model.UserCredentials;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    User findByPhoneNumber(String phoneNumber);

    UserCredentials getUserCredentialsByUserID(Integer userID);
}
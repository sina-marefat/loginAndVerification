package com.example.verfication.emailVerify.registration.token;

import com.example.verfication.emailVerify.appuser.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Long> {
    ConfirmationToken findByToken(String token);
    void deleteByAppUser(AppUser appUser);
    ConfirmationToken findByAppUser(AppUser appUser);
    boolean existsByAppUser(AppUser appUser);
}

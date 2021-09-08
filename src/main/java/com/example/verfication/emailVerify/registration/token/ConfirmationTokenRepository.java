package com.example.verfication.emailVerify.registration.token;

import com.example.verfication.emailVerify.appuser.AppUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfirmationTokenRepository {


    Boolean saveToken(ConfirmationToken confirmationToken);

    List<ConfirmationToken> fetchAll();


    void deleteToken(AppUser appUser);
}

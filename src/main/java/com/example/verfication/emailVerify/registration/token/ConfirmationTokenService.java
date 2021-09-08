package com.example.verfication.emailVerify.registration.token;


import com.example.verfication.emailVerify.appuser.AppUser;

import java.util.List;

public interface ConfirmationTokenService {
    Boolean saveToken(ConfirmationToken confirmationToken);

    List<ConfirmationToken> fetchAll();

    ConfirmationToken getToken(String token);


    void deleteToken(AppUser appUser);

}

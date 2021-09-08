package com.example.verfication.emailVerify.registration.token;

import com.example.verfication.emailVerify.appuser.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService{
    @Autowired
    private  ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public Boolean saveToken(ConfirmationToken confirmationToken) {
        return confirmationTokenRepository.saveToken(confirmationToken);
    }

    @Override
    public List<ConfirmationToken> fetchAll() {
        return confirmationTokenRepository.fetchAll();
    }

    @Override
    public ConfirmationToken getToken(String token) {
        List<ConfirmationToken> tokens = fetchAll();
        ConfirmationToken found =tokens.stream().filter(s -> s.getToken().equals(token)).findFirst().orElse(null);
        return found;
    }


    @Override
    public void deleteToken(AppUser appUser) {
        confirmationTokenRepository.deleteToken(appUser);
    }

}

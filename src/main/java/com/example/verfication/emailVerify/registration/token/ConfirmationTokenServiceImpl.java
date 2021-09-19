package com.example.verfication.emailVerify.registration.token;

import com.example.verfication.emailVerify.appuser.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService{
    @Autowired
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public void saveToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public List<ConfirmationToken> fetchAll() {
        return confirmationTokenRepository.findAll();
    }

    @Override
    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }


    @Override
    public void deleteToken(AppUser appUser) {
        confirmationTokenRepository.deleteByAppUser(appUser);
    }

}

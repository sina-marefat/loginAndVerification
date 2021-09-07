package com.example.verfication.emailVerify.registration.token;

import com.example.verfication.emailVerify.appuser.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken confirmationToken){
        confirmationTokenRepository.save(confirmationToken);
    }

    public ConfirmationToken getToken(String id){
        if(!confirmationTokenRepository.findByToken(id).isPresent()){
            throw new IllegalStateException("Token Not Found");
        }
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(id).get();
        return confirmationToken;
    }

    public void setConfirmedAt(String token) {
        if(!confirmationTokenRepository.findByToken(token).isPresent()){
            throw new IllegalStateException("this token is not present");
        }
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).get();
        confirmationToken.setConfirmedAt(LocalDateTime.now());

    }

    public Boolean hasActiveToken(AppUser appUser){
        List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findAllByAppUser(appUser);
        for (ConfirmationToken c: confirmationTokens) {
            if(!c.getIs_used()){
                return false;
            }
        }
        return true;
    }


}

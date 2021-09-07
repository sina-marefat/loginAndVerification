package com.example.verfication.emailVerify.appuser;

import com.example.verfication.emailVerify.registration.token.ConfirmationToken;
import com.example.verfication.emailVerify.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND = "user with email %s not found";

    private final AppUserRepository appUserRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND,email)));
    }
    @Transactional
    public String signUpUser(AppUser appUser){
        if(appUserRepository.findByEmail(appUser.getEmail()).isPresent()){
            throw new IllegalStateException("Email already taken");
        }
        String encodedPassword  = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);
        String token_id = UUID.randomUUID().toString();
        ConfirmationToken token = new ConfirmationToken(token_id, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5),appUser);
        confirmationTokenService.saveConfirmationToken(token);
        return token_id;
    }

    public void enableAppUser(String email) {
        if(!appUserRepository.findByEmail(email).isPresent()){
            throw new IllegalStateException("this user is not defined");
        }
        AppUser appUser = appUserRepository.findByEmail(email).get();
        appUser.setEnabled(true);
    }

    public String renewalToken(AppUser appUser){
        String token_id = UUID.randomUUID().toString();
        ConfirmationToken token = new ConfirmationToken(token_id, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5),appUser);
        confirmationTokenService.saveConfirmationToken(token);
        return token_id;
    }
}

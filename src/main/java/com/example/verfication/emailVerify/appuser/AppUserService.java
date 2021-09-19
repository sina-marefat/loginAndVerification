package com.example.verfication.emailVerify.appuser;

import com.example.verfication.emailVerify.registration.token.ConfirmationToken;
import com.example.verfication.emailVerify.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@Service
@Data
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND = "user with email %s not found";

    private final AppUserRepository appUserRepository;

    private  final BCryptPasswordEncoder bCryptPasswordEncoder;

    private  final ConfirmationTokenService confirmationTokenService;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND,email)));
    }
    @Transactional
    public String signUpUser(AppUser appUser) throws IllegalAccessException{
        if(appUserRepository.findByEmail(appUser.getEmail()).isPresent()){
            throw new IllegalAccessException("Email already taken");
        }
        String encodedPassword  = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);
        return makeToken(appUser).getToken();
    }

    public ConfirmationToken makeToken(AppUser appUser){
        String token_id = UUID.randomUUID().toString();
        ConfirmationToken token = new ConfirmationToken(token_id, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),appUser);
        confirmationTokenService.saveToken(token);
        return token;
    }


    public void enableAppUser(String email){
        if(appUserRepository.findByEmail(email).isEmpty()){
            throw new UsernameNotFoundException("this user is not defined");
        }
        AppUser appUser = appUserRepository.findByEmail(email).get();
        appUser.setEnabled(true);
    }

    public void setActivationTime(String email){
        if(appUserRepository.findByEmail(email).isEmpty()){
            throw new UsernameNotFoundException("this user is not defined");
        }
        AppUser appUser = appUserRepository.findByEmail(email).get();
        appUser.setActivationTime(LocalDateTime.now());
    }

    public List<AppUser> fetchAllUsers() {
        return appUserRepository.findAll();
    }
}

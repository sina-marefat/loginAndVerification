package com.example.verfication.emailVerify.registration.token;

import com.example.verfication.emailVerify.appuser.AppUser;
import com.example.verfication.emailVerify.appuser.AppUserRepository;
import com.example.verfication.emailVerify.appuser.AppUserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ConfirmationTokenRepositoryTest {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void canFindTokenByTokenId() {
        AppUser appUser = new AppUser("sina","marefat","sinamatefat@gmail.com","1234", AppUserRole.ADMIN);
        String token_id = UUID.randomUUID().toString();
        ConfirmationToken token = new ConfirmationToken(token_id, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),appUser);
        appUserRepository.save(appUser);
        confirmationTokenRepository.save(token);
        assertThat(confirmationTokenRepository.findByToken(token_id)).isEqualTo(token);
    }

    @Test
    void canDeleteTokenByAppUser() {
        AppUser appUser = new AppUser("sina","marefat","sinamatefat@gmail.com","1234", AppUserRole.ADMIN);
        String token_id = UUID.randomUUID().toString();
        ConfirmationToken token = new ConfirmationToken(token_id, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),appUser);
        appUserRepository.save(appUser);
        confirmationTokenRepository.save(token);
        confirmationTokenRepository.delete(token);
        assertThat(confirmationTokenRepository.existsByAppUser(appUser)).isFalse();
        assertThat(confirmationTokenRepository.findByToken(token_id)).isNull();
    }

    @Test
    void canFindByAppUser() throws InterruptedException {
        AppUser appUser = new AppUser("sina","marefat","sinamatefat@gmail.com","1234", AppUserRole.ADMIN);
        String token_id = UUID.randomUUID().toString();
        ConfirmationToken token = new ConfirmationToken(token_id, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),appUser);
        appUserRepository.save(appUser);
        confirmationTokenRepository.save(token);
        assertThat(confirmationTokenRepository.findByAppUser(appUser)).isEqualTo(token);
    }

}
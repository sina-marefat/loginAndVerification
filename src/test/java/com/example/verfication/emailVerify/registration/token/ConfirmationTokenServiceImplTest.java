package com.example.verfication.emailVerify.registration.token;

import com.example.verfication.emailVerify.appuser.AppUser;
import com.example.verfication.emailVerify.appuser.AppUserRepository;
import com.example.verfication.emailVerify.appuser.AppUserRole;
import com.example.verfication.emailVerify.appuser.AppUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DataJpaTest
class ConfirmationTokenServiceImplTest {
    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;
    private ConfirmationTokenServiceImpl underTest;
    @Autowired private AppUserRepository appUserRepository;
    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new ConfirmationTokenServiceImpl(confirmationTokenRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void checkIfCanSaveToken() {
        AppUser appUser = new AppUser("sina","marefat","sinamatefat@gmail.com","1234", AppUserRole.ADMIN);
        String token_id = UUID.randomUUID().toString();
        ConfirmationToken token = new ConfirmationToken(token_id, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),appUser);
        appUserRepository.save(appUser);
        underTest.saveToken(token);
        verify(confirmationTokenRepository).save(token);
    }

    @Test
    void checkIfCanFetchAll() {
        underTest.fetchAll();
        verify(confirmationTokenRepository).findAll();
    }

    @Test
    void checkIfCanGetTokenByTokenId() {
        AppUser appUser = new AppUser("sina","marefat","sinamatefat@gmail.com","1234", AppUserRole.ADMIN);
        String token_id = UUID.randomUUID().toString();
        ConfirmationToken token = new ConfirmationToken(token_id, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),appUser);
        appUserRepository.save(appUser);
        given(confirmationTokenRepository.findByToken(token_id)).willReturn(token);
        assertThat(underTest.getToken(token_id)).isEqualTo(token);
    }

    @Test
    void deleteToken() {

    }
}
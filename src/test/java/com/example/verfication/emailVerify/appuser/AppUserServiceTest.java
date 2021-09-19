package com.example.verfication.emailVerify.appuser;

import com.example.verfication.emailVerify.registration.token.ConfirmationToken;
import com.example.verfication.emailVerify.registration.token.ConfirmationTokenRepository;
import com.example.verfication.emailVerify.registration.token.ConfirmationTokenService;
import com.example.verfication.emailVerify.registration.token.ConfirmationTokenServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DataJpaTest
class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;
    private AppUserService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;


    @BeforeEach

    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        ConfirmationTokenServiceImpl confirmationTokenServiceImpl = new ConfirmationTokenServiceImpl(confirmationTokenRepository);
        underTest = new AppUserService(appUserRepository, new BCryptPasswordEncoder(), confirmationTokenServiceImpl);

    }

    @Test
    void canGetAllAppUsers(){
        //when
        underTest.fetchAllUsers();
        //then
        verify(appUserRepository).findAll();
    }

    @Test
    void canLoadByUserName(){
        //given
        String email = "ali@gmail.com";
        AppUser appUser = new AppUser("ali","hossein",email,"1234",AppUserRole.USER);
        given(appUserRepository.findByEmail(email)).willReturn(java.util.Optional.of(appUser));
        UserDetails found = underTest.loadUserByUsername(email);
        verify(appUserRepository).findByEmail(email);
        assertThat(found.getUsername()).isEqualTo(appUser.getEmail());
        assertThat(found.getPassword()).isEqualTo(appUser.getPassword());
    }

    @Test
    void canNotLoadByUserName(){
        String email = "ali@gmail.com";
        AppUser appUser = new AppUser("ali","hossein","alihossein@gmail.com","1234",AppUserRole.USER);
        given(appUserRepository.findByEmail(email)).willThrow(UsernameNotFoundException.class);
        given(appUserRepository.findByEmail("alihossein@gmail.com")).willReturn(java.util.Optional.of(appUser));
        assertThatThrownBy(() -> underTest.loadUserByUsername(email)).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void signsUpUser() throws IllegalAccessException {
        String email = "ali@gmail.com";
        AppUser appUserExists = new AppUser("ali","hossein",email,"4567",AppUserRole.USER);
        String password = "1234";
        AppUser appUser = new AppUser("ali","hossein","alihossein@gmail.com",password,AppUserRole.USER);
        underTest.signUpUser(appUser);
        given(appUserRepository.findByEmail(email)).willReturn(Optional.of(appUserExists));
        verify(appUserRepository).save(appUser);
    }

    @Test
    void doesntSignsUpUser(){
        String email = "ali@gmail.com";
        String password = "1234";
        AppUser appUser = new AppUser("ali","hossein",email,password,AppUserRole.USER);
        given(appUserRepository.findByEmail(email)).willReturn(Optional.of(appUser));
        assertThatThrownBy(() -> underTest.signUpUser(appUser)).isInstanceOf(IllegalAccessException.class).hasMessageContaining("Email already taken");
        verify(appUserRepository,never()).save(appUser);
    }

    @Test
    void canMakeToken(){
        String email = "ali@gmail.com";
        String password = "1234";
        AppUser appUser = new AppUser("ali","hossein",email,password,AppUserRole.USER);
        ConfirmationToken madeToken = underTest.makeToken(appUser);
        verify(confirmationTokenRepository).save(madeToken);
    }

    @Test
    void canEnableAppUser(){
        String email = "ali@gmail.com";
        String password = "1234";
        AppUser appUser = new AppUser("ali","hossein",email,password,AppUserRole.USER);
        given(appUserRepository.existsByEmail(email)).willReturn(true);
        given(appUserRepository.findByEmail(email)).willReturn(Optional.of(appUser));
        underTest.enableAppUser(email);
        assertThat(appUser.isEnabled()).isTrue();
        verify(appUserRepository,times(2)).findByEmail(email);

    }

    @Test
    void canNotEnableAppUser() {
        String email = "ali@gmail.com";
        String notIncludedEmail = "alii@gmail.com";
        String password = "1234";
        AppUser appUser = new AppUser("ali","hossein",email,password,AppUserRole.USER);
        given(appUserRepository.existsByEmail(email)).willReturn(true);
        given(appUserRepository.existsByEmail(notIncludedEmail)).willReturn(false);
        given(appUserRepository.findByEmail(email)).willReturn(Optional.of(appUser));
        assertThatThrownBy(() ->underTest.enableAppUser(notIncludedEmail)).
                isInstanceOf(UsernameNotFoundException.class).
                hasMessageContaining("this user is not defined");
        verify(appUserRepository,times(1)).findByEmail(notIncludedEmail);
        assertThat(appUser.isEnabled()).isFalse();
    }

    @Test
    void canSetActivationTime(){
        LocalDateTime before = LocalDateTime.now();
        String email = "ali@gmail.com";
        String password = "1234";
        AppUser appUser = new AppUser("ali","hossein",email,password,AppUserRole.USER);
        given(appUserRepository.existsByEmail(email)).willReturn(true);
        given(appUserRepository.findByEmail(email)).willReturn(Optional.of(appUser));
        underTest.setActivationTime(email);
        assertThat(appUser.getActivationTime()).isNotNull();
        assertThat(appUser.getActivationTime()).isBetween(before,LocalDateTime.now());
    }
    @Test
    void canNotSetActivationTime(){
        String email = "ali@gmail.com";
        String notIncludedEmail = "alii@gmail.com";
        String password = "1234";
        AppUser appUser = new AppUser("ali","hossein",email,password,AppUserRole.USER);
        given(appUserRepository.existsByEmail(email)).willReturn(true);
        given(appUserRepository.existsByEmail(notIncludedEmail)).willReturn(false);
        given(appUserRepository.findByEmail(email)).willReturn(Optional.of(appUser));
        assertThatThrownBy(()->underTest.setActivationTime(notIncludedEmail)).
                isInstanceOf(UsernameNotFoundException.class).
                hasMessageContaining("this user is not defined");
        assertThat(appUser.getActivationTime()).isNull();
        verify(appUserRepository,times(1)).findByEmail(notIncludedEmail);
        verify(appUserRepository,never()).findByEmail(email);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
}
package com.example.verfication.emailVerify.registration;

import com.example.verfication.emailVerify.appuser.AppUser;
import com.example.verfication.emailVerify.appuser.AppUserRole;
import com.example.verfication.emailVerify.appuser.AppUserService;
import com.example.verfication.emailVerify.mail.EmailSender;
import com.example.verfication.emailVerify.registration.token.ConfirmationToken;
import com.example.verfication.emailVerify.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.security.cert.CertificateExpiredException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DataJpaTest
class RegistrationServiceTest {

    private RegistrationService underTest;
    private final AppUserService appUserService = Mockito.mock(AppUserService.class);
    private final EmailValidator emailValidator = Mockito.mock(EmailValidator.class);
    private final ConfirmationTokenService confirmationTokenService = Mockito.mock(ConfirmationTokenService.class);
    private final EmailSender emailSender = Mockito.mock(EmailSender.class);




    @BeforeEach
    void setUp() {
        underTest = new RegistrationService(appUserService,emailValidator,confirmationTokenService,emailSender);
    }

    @Test
    void canRegisterNewUser() throws IllegalAccessException {
        RegistrationRequest request= new RegistrationRequest("sina","marefat","sinamatefat@gmail.com","1234");
        given(emailValidator.test(anyString())).willReturn(true);
        underTest.register(request);
        verify(emailSender,times(1)).send(anyString(),anyString());
        verify(appUserService,times(1)).signUpUser(any());
    }
    @Test
    void canNotRegisterNewUser() throws IllegalAccessException {
        RegistrationRequest request= new RegistrationRequest("sina","marefat","sinamatefat@gmail.com","1234");
        given(emailValidator.test(anyString())).willReturn(false);
        assertThatThrownBy(() -> underTest.register(request)).
                isInstanceOf(IllegalStateException.class).
                hasMessageContaining("Email "+request.getEmail() +" is not valid");
    }

    @Test
    void canSendEmail(){
        String email = "sinamatefat@gmail.com";
        String token = "1";
        String name = "sina";
        String link = "http://localhost:8080/api/v1/registration/confirm?token="+token;
        underTest.sendEmail(email,name,link);
        verify(emailSender,times(1)).send(anyString(),anyString());
    }

    @Test
    void canConfirmTrueToken() throws CertificateExpiredException {
        String email = "ali@gmail.com";
        String notIncludedEmail = "alii@gmail.com";
        String password = "1234";
        String token = UUID.randomUUID().toString();
        AppUser appUser = new AppUser("ali","hossein",email,password, AppUserRole.USER);
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(1),
                        appUser);
        given(confirmationTokenService.getToken(token)).willReturn(confirmationToken);
        assertThat(underTest.confirmationToken(token)).isEqualTo("CONFIRMED");
        verify(confirmationTokenService,times(1)).deleteToken(appUser);
        verify(appUserService,times(1)).enableAppUser(email);
        verify(appUserService,times(1)).setActivationTime(email);
    }

    @Test
    void canNotConfirmFalseToken(){
        String email = "ali@gmail.com";
        String notIncludedEmail = "alii@gmail.com";
        String password = "1234";
        String token = UUID.randomUUID().toString();
        AppUser appUser = new AppUser("ali","hossein",email,password, AppUserRole.USER);
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(1),
                        appUser);
        given(confirmationTokenService.getToken(token)).willReturn(null);
        assertThatThrownBy(() -> underTest.confirmationToken(token)).
                isInstanceOf(NoSuchElementException.class).
                hasMessageContaining("this token is used or not found");
        verify(appUserService,never()).makeToken(appUser);
        verify(confirmationTokenService,never()).deleteToken(appUser);
        verify(appUserService,never()).enableAppUser(email);
        verify(appUserService,never()).setActivationTime(email);
    }

    @Test
    void shouldMakeNewTokenCauseExpired() throws CertificateExpiredException {
        String email = "ali@gmail.com";
        String password = "1234";
        String token = UUID.randomUUID().toString();
        AppUser appUser = new AppUser("ali","hossein",email,password, AppUserRole.USER);
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(2),
                        appUser);
        ConfirmationToken newConfirmationToken =
                new ConfirmationToken(UUID.randomUUID().toString(),
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(2),
                        appUser);
        confirmationToken.setExpiredAt(LocalDateTime.now());
        given(appUserService.makeToken(appUser)).willReturn(newConfirmationToken);
        given(confirmationTokenService.getToken(token)).willReturn(confirmationToken);
        assertThat(underTest.confirmationToken(token)).isEqualTo("YOUR TOKEN HAS BEEN EXPIRED WE HAVE SEND NEW ONE");
        verify(appUserService,times(1)).makeToken(appUser);
        verify(emailSender,times(1)).send(anyString(),anyString());
        verify(confirmationTokenService,times(1)).deleteToken(appUser);
    }
}
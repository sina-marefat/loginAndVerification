package com.example.verfication.emailVerify.appuser;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private  AppUserRepository appUserRepository;

    @Test
    void itShouldCheckFindByEmailExists() {
        String email = "sinamatefat@gmail.com";
        AppUser appUser = new AppUser("sina","marefat",email,"123",AppUserRole.USER);
        appUserRepository.save(appUser);
        assertThat(appUserRepository.existsByEmail(email)).isTrue();
    }

    @Test
    void itShouldCheckFindByEmailDoesntExists(){
        String email = "sinamatefat@gmail.com";
        AppUser appUser = new AppUser("sina","marefat","siinamatefat@gmail.com","123",AppUserRole.USER);
        appUserRepository.save(appUser);
        assertThat(appUserRepository.existsByEmail(email)).isFalse();
    }

    @Test
    void itShouldFindByEmail(){
        String email = "marefat@hotmail.com";
        AppUser appUser = new AppUser("sina","marefat",email,"123",AppUserRole.USER);
        appUserRepository.save(appUser);
        Optional<AppUser> appUserFound = appUserRepository.findByEmail(email);
        assertThat(appUser).isEqualTo(appUserFound.get());
    }

    @Test
    void itShouldentFindByEmail(){
        String email = "marefat@hotmail.com";
        AppUser appUser = new AppUser("sina","marefat","sina@marefat.com","123",AppUserRole.USER);
        appUserRepository.save(appUser);
        Optional<AppUser> appUserFound = appUserRepository.findByEmail(email);
        assertThatThrownBy(appUserFound::get).isInstanceOf(NoSuchElementException.class);
    }
    @Test
    void itShouldentsaveDuplicate(){
        String email = "marefat@hotmail.com";
        AppUser appUser = new AppUser("sina","marefat","sina@marefat.com","123",AppUserRole.USER);
        AppUser appUser2 = new AppUser("ali","m","sina@marefat.com","345",AppUserRole.ADMIN);
        appUserRepository.save(appUser);
        appUserRepository.save(appUser2);

    }
    //NoSuchElementException
}
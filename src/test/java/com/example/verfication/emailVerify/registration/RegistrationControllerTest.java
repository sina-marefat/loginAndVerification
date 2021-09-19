package com.example.verfication.emailVerify.registration;

import com.example.verfication.emailVerify.appuser.AppUser;
import com.example.verfication.emailVerify.appuser.AppUserRole;
import com.example.verfication.emailVerify.appuser.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = RegistrationController.class)
class RegistrationControllerTest {

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;
    @MockBean
    private RegistrationService registrationService;
    @MockBean
    private AppUserService appUserService;
    @Autowired
    private MockMvc mvc;


    @Test
    public void shouldFetchAllUsers() throws Exception {
        String email = "ali@gmail.com";
        String password = "1234";
        AppUser appUser = new AppUser("ali","hossein",email,password, AppUserRole.USER);
        given(appUserService.fetchAllUsers()).willReturn(List.of(appUser));
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/users")).andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()));
        verify(appUserService,times(1)).fetchAllUsers();
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        String token = UUID.randomUUID().toString();
        given(registrationService.register(any())).willReturn(token);
        String res = mvc.perform(MockMvcRequestBuilders.post("/api/v1/registration").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "    \"firstName\":\"sina\",\n" +
                "    \"lastName\":\"marefat\",\n" +
                "    \"email\":\"sinamatefat@gmail.com\",\n" +
                "    \"password\":\"Sina09212154938\"\n" +
                "    \n" +
                "}"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();
        given(registrationService.register(any())).willReturn(token);
        assertThat(res).isEqualTo(token);
        verify(registrationService,times(1)).register(any(RegistrationRequest.class));
    }

    @Test
    public void shouldRegisterAndActivate() throws Exception {
        String token = UUID.randomUUID().toString();
        given(registrationService.register(any())).willReturn(token);
        String res = mvc.perform(MockMvcRequestBuilders.post("/api/v1/registration").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "    \"firstName\":\"sina\",\n" +
                "    \"lastName\":\"marefat\",\n" +
                "    \"email\":\"sinamatefat@gmail.com\",\n" +
                "    \"password\":\"Sina09212154938\"\n" +
                "    \n" +
                "}"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();
        System.out.println(res);
        given(registrationService.confirmationToken(token)).willReturn("CONFIRMED");
        assertThat("CONFIRMED").
                isEqualTo(mvc.perform(MockMvcRequestBuilders.
                        get("/api/v1/registration/confirm")
                        .param("token",token)).andReturn().getResponse().getContentAsString());
        verify(registrationService,times(1)).register(any(RegistrationRequest.class));
        verify(registrationService,times(1)).confirmationToken(token);
    }

    @Test
    public void checkFakeOrConfirmedToken() throws Exception {
        String fakeToken = UUID.randomUUID().toString();
        String respString = "this token is used or not found";
        given(registrationService.confirmationToken(fakeToken)).willReturn(respString);
        assertThat(respString).isEqualTo(mvc.perform(MockMvcRequestBuilders.
                get("/api/v1/registration/confirm")
                .param("token",fakeToken)).andReturn().getResponse().getContentAsString());
        verify(registrationService,times(1)).confirmationToken(fakeToken);
    }

    @Test
    public void checkExpiredToken() throws Exception {
        String expiredToken = UUID.randomUUID().toString();
        String respString = "YOUR TOKEN HAS BEEN EXPIRED WE HAVE SEND NEW ONE";
        given(registrationService.confirmationToken(expiredToken)).willReturn(respString);
        assertThat(respString).isEqualTo(mvc.perform(MockMvcRequestBuilders.
                get("/api/v1/registration/confirm")
                .param("token",expiredToken)).andReturn().getResponse().getContentAsString());
        verify(registrationService,times(1)).confirmationToken(expiredToken);
    }

    @Disabled
    @Test
    public void shouldNotRegisterUserDuplicateEmail() throws Exception {
        given(registrationService.register(any())).willThrow(IllegalAccessException.class);
        try {
            mvc.perform(MockMvcRequestBuilders.post("/api/v1/registration").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                    "    \"firstName\":\"sina\",\n" +
                    "    \"lastName\":\"marefat\",\n" +
                    "    \"email\":\"sinamatefat@gmail.com\",\n" +
                    "    \"password\":\"Sina09212154938\"\n" +
                    "    \n" +
                    "}"))
                    .andExpect(MockMvcResultMatchers.status().is(500));
        } catch (NestedServletException e){
            e.getMessage();
        }
    }

    @Disabled
    @Test
    public void shouldNotRegisterUser() throws Exception {
        given(appUserService.signUpUser(any())).willThrow(IllegalAccessException.class);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/registration").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "    \"firstName\":\"sina\",\n" +
                "    \"lastName\":\"marefat\",\n" +
                "    \"email\":\"sinamatefat@gmail.com\"\n"+
                "    \n" +
                "}"))
                .andExpect(MockMvcResultMatchers.status().is(200));
        //verify(registrationService,never()).register(any());
    }

    @Test
    public void corruptedJson() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/registration").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "    \"firstName\":\"sina\",\n" +
                "    \"lastName\":\"marefat\",\n" +
                "    \"email\":\"sinamatefat@gmail.com\",\n"+
                "    \n" +
                "}"))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }
}
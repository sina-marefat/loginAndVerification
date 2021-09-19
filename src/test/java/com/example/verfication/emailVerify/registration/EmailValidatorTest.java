package com.example.verfication.emailVerify.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    private EmailValidator emailValidator;

    @BeforeEach
    void setUp() {
        emailValidator = new EmailValidator();
    }

    @Test
    void test1() {
        Boolean test = emailValidator.test("sina@gmail.com");
        assertThat(test).isTrue();
    }


    @Test
    void test2() {
        Boolean test = emailValidator.test("sina@gmail.c");
        assertThat(test).isFalse();
    }


    @Test
    void test3() {
        Boolean test = emailValidator.test("sina@gmailcom");
        assertThat(test).isFalse();
    }


    @Test
    void test4() {
        Boolean test = emailValidator.test("sinagmail.com");
        assertThat(test).isFalse();
    }
}
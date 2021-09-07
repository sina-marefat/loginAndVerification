package com.example.verfication.emailVerify.registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {

    @Override
    public boolean test(String s) {
        return s.matches(".{2,}[@].{3,}[.].{2,}");
    }
}

package com.example.verfication.emailVerify.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.cert.CertificateExpiredException;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request){
        return registrationService.register(request);
    }

    @GetMapping(path = "/confirm")
    public String confirm(@RequestParam("token") String token) throws CertificateExpiredException {
        return registrationService.confirmationToken(token);
    }
}

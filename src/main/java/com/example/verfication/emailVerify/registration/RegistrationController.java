package com.example.verfication.emailVerify.registration;

import com.example.verfication.emailVerify.appuser.AppUser;
import com.example.verfication.emailVerify.appuser.AppUserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.cert.CertificateExpiredException;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    private final AppUserService appUserService;

    @PostMapping("/registration")
    public String register(@RequestBody RegistrationRequest request) throws IllegalAccessException {
        return registrationService.register(request);
    }

    @GetMapping("/admin/users")
    public List<AppUser> fetchAllUsers(){
        return appUserService.fetchAllUsers();
    }

    @GetMapping(path = "/registration/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) throws CertificateExpiredException {
        return ResponseEntity.ok(registrationService.confirmationToken(token));
    }
}

package com.example.verfication.emailVerify.registration.token;


import com.example.verfication.emailVerify.appuser.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public class ConfirmationTokenRepositoryImpl implements ConfirmationTokenRepository{

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String KEY = "TOKEN";


    @Override
    public Boolean saveToken(ConfirmationToken confirmationToken) {
        try {
            redisTemplate.opsForHash().put(KEY,confirmationToken.getAppUser().getEmail(),confirmationToken);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ConfirmationToken> fetchAll() {
        List<ConfirmationToken> confirmationTokens = redisTemplate.opsForHash().values(KEY);
        return confirmationTokens;
    }

    @Override
    public void deleteToken(AppUser appUser) {
        redisTemplate.opsForHash().delete(KEY,appUser.getEmail());
    }

}

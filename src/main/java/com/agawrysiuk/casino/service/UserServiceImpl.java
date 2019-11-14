package com.agawrysiuk.casino.service;

import com.agawrysiuk.casino.model.database.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final CasinoUserRepository casinoUserRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(CasinoUserRepository casinoUserRepository, UserRepository userRepository) {
        this.casinoUserRepository = casinoUserRepository;
        this.userRepository = userRepository;
    }


    // == Registration ==
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    };

    public User findByConfirmationToken(String confirmationToken) {
        return userRepository.findByConfirmationToken(confirmationToken);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    @Override
    public User registerNewUserAccount(UserDto accountDto) {

        log.info("registerNewUserAccount() started");

        User user = new User();
        user.setUsername(accountDto.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        user.setEnabled(true);
        user.setAuthority("ROLE_USER");
        log.info("user = {}",user);
        userRepository.save(user);

        return user;
    }

    private boolean emailExists(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

    // == Game ==
    public CasinoUser findUsername(String nickname) {
        return casinoUserRepository.findByNickname(nickname);
    }

    public void updateBalance(double balance, String nickname) {
        casinoUserRepository.updateBalance(balance, nickname);
    }
}

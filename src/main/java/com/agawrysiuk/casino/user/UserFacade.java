package com.agawrysiuk.casino.user;

import com.agawrysiuk.casino.config.jwt.JwtUtils;
import com.agawrysiuk.casino.config.security.userdetails.UserDetailsImpl;
import com.agawrysiuk.casino.user.exception.EmailExistsException;
import com.agawrysiuk.casino.user.exception.UsernameExistsException;
import com.agawrysiuk.casino.user.request.LoginRequest;
import com.agawrysiuk.casino.user.response.JwtResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public ResponseEntity<?> register(UserDto userDto) {
        checkAvailability(userDto);
        userService.registerNewUserAccount(userDto, false);
        return ResponseEntity.ok("Registration successful!");
    }

    private void checkAvailability(UserDto userDto) {
        if (userService.existsByUsername(userDto.getUsername())) {
            throw new UsernameExistsException();
        }
        if (userService.existsByEmail(userDto.getEmail())) {
            throw new EmailExistsException();
        }
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }
}

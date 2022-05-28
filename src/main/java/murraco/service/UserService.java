package murraco.service;

import lombok.RequiredArgsConstructor;
import murraco.dto.UserDataDTO;
import murraco.exception.CustomException;
import murraco.model.AppUser;
import murraco.repository.UserRepository;
import murraco.security.JwtTokenProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public String signin(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            AppUser appUser = userRepository.findByUsername(username).get();
            return jwtTokenProvider.createToken(username, appUser.getInstituteId(), appUser.getAppUserRoles());
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signup(AppUser appUser) {
        Optional<AppUser> exist = userRepository.findByUsername(appUser.getUsername());
        if (exist.isPresent())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Username already exist");
        if (appUser.getAppUserRoles() == null || appUser.getAppUserRoles().isEmpty() || StringUtils.isAllEmpty(appUser.getPassword(), appUser.getInstituteId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        userRepository.save(appUser);
        return jwtTokenProvider.createToken(appUser.getUsername(), appUser.getInstituteId(), appUser.getAppUserRoles());
    }

    public String editUser(UserDataDTO appUser) {
        AppUser existing = userRepository.findByUsername(appUser.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (appUser.getAppUserRoles() != null && !appUser.getAppUserRoles().isEmpty()) {
            existing.setAppUserRoles(appUser.getAppUserRoles());
        }
        if (!StringUtils.isEmpty(appUser.getNewUsername()) && !appUser.getNewUsername().equals(existing.getUsername())) {
            Optional<AppUser> newUsernameExists = userRepository.findByUsername(appUser.getNewUsername());
            if (newUsernameExists.isPresent())
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
            existing.setUsername(appUser.getUsername());
        }
        if (!StringUtils.isEmpty(appUser.getPassword())) {
            existing.setPassword(passwordEncoder.encode(appUser.getPassword()));
        }
        if (!StringUtils.isEmpty(appUser.getInstituteId()))
            existing.setInstituteId(appUser.getInstituteId());
        userRepository.save(existing);
        return jwtTokenProvider.createToken(existing.getUsername(), existing.getInstituteId(), existing.getAppUserRoles());
    }

    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }

    public AppUser search(String username) {
        AppUser appUser = userRepository.findByUsername(username).get();
        if (appUser == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return appUser;
    }

    public AppUser whoami(HttpServletRequest req) {
        return userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req))).get();
    }

    public String refresh(String username) {
        AppUser appUser = userRepository.findByUsername(username).get();
        return jwtTokenProvider.createToken(username, appUser.getInstituteId(), appUser.getAppUserRoles());
    }

}

package com.pablo.taskendaback.service;

import com.pablo.taskendaback.dtos.NewUserDto;
import com.pablo.taskendaback.enums.RoleList;
import com.pablo.taskendaback.jwt.JwtUtil;
import com.pablo.taskendaback.model.Role;
import com.pablo.taskendaback.model.User;
import com.pablo.taskendaback.repository.RoleRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CookieService cookieService;

    @Autowired
    public AuthService(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,AuthenticationManagerBuilder authenticationManagerBuilder,CookieService cookieService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.cookieService=cookieService;
    }

    public String authenticate(String username, String password, HttpServletResponse response){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authresult = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authresult);

        String jwt = jwtUtil.generateToken(authresult);
        cookieService.addHttpOnlyCookie("jwt",jwt,60*60*24*7,response);

        User user = userService.findByUsername(username);

        return user.getRole().getName().toString();
    }

    public void register(NewUserDto newUserDto){
        if (userService.existsByUsername(newUserDto.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        Role roleUser = roleRepository.findByName(RoleList.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        User user = new User(newUserDto.getUsername(),passwordEncoder.encode(newUserDto.getPassword()),newUserDto.getEmail(),roleUser);
        userService.save(user);
    }

    public void logout(HttpServletResponse response){
        cookieService.deleteCookie("jwt",response);
    }

}

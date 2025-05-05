package com.pablo.taskendaback.service;

import com.pablo.taskendaback.dtos.NewUserDto;
import com.pablo.taskendaback.enums.RoleList;
import com.pablo.taskendaback.jwt.JwtUtil;
import com.pablo.taskendaback.model.Role;
import com.pablo.taskendaback.model.User;
import com.pablo.taskendaback.repository.RoleRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.pablo.taskendaback.service.UserService;

@Service
public class AuthService {
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthService(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public String authenticate(String username, String password){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authresult = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authresult);
        return jwtUtil.generateToken(authresult);
    }

    public void register(NewUserDto newUserDto){
        if (userService.existsByUsername(newUserDto.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        Role roleUser = roleRepository.findByName(RoleList.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        User user = new User(newUserDto.getUsername(),passwordEncoder.encode(newUserDto.getPassword()),newUserDto.getEmail(),roleUser);
        userService.save(user);
    }
}

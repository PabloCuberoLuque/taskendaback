package com.pablo.taskendaback.controller;

import com.pablo.taskendaback.dtos.ApiMessage;
import com.pablo.taskendaback.dtos.LoginUserDto;
import com.pablo.taskendaback.dtos.NewUserDto;
import com.pablo.taskendaback.model.User;
import com.pablo.taskendaback.service.AuthService;
import com.pablo.taskendaback.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiMessage> login(@Valid @RequestBody LoginUserDto loginUserDto, BindingResult bindingResult, HttpServletResponse response){
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new ApiMessage("Check your credentials"));
        }
        try{
            String role = authService.authenticate(loginUserDto.getUsername(), loginUserDto.getPassword(),response);
            return ResponseEntity.ok(new ApiMessage(role));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiMessage(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiMessage> register(@Valid @RequestBody NewUserDto newUserDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new ApiMessage("Check the fields"));
        }
        try{
            authService.register(newUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiMessage("User registered successfully"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new ApiMessage(e.getMessage()));
        }
    }

    @GetMapping("/check-auth")
    public ResponseEntity<String> checkAuth(){
        return ResponseEntity.ok("You are authorized");
    }

    @GetMapping("/user/details")
    public ResponseEntity<User> getAuthenticatedUser(){
        User user = userService.getUserDetails();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        authService.logout(response);
        return ResponseEntity.ok(new ApiMessage("Logged out successfully"));
    }
}

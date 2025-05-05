package com.pablo.taskendaback.controller;

import com.pablo.taskendaback.dtos.LoginUserDto;
import com.pablo.taskendaback.dtos.NewUserDto;
import com.pablo.taskendaback.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginUserDto loginUserDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body("Check your credentials");
        }
        try{
            String jwt = authService.authenticate(loginUserDto.getUsername(), loginUserDto.getPassword());
            return ResponseEntity.ok(jwt);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Check your credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody NewUserDto newUserDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body("Check the fields");
        }
        try{
            authService.register(newUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Check the fields");
        }
    }

    @GetMapping("/check-auth")
    public ResponseEntity<String> checkAuth(){
        return ResponseEntity.ok("You are authorized");
    }

}

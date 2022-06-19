package com.decagon.chompapp.services;

import com.decagon.chompapp.dtos.JwtAuthResponse;
import com.decagon.chompapp.dtos.LoginDto;
import com.decagon.chompapp.dtos.UserDto;
import com.decagon.chompapp.models.User;
import org.springframework.http.ResponseEntity;

public interface LoginService {
    ResponseEntity<UserDto> login(LoginDto loginDto) throws Exception;
//    ResponseEntity<JwtAuthResponse> login(LoginDto loginDto) throws Exception;
    ResponseEntity<?> logout(String token);


}

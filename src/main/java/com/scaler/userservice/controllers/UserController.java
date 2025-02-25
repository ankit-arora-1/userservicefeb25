package com.scaler.userservice.controllers;

import com.scaler.userservice.dtos.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    public LoginResponseDto login (@RequestBody LoginRequestDto requestDto) {
        return null;
    }

    public SignupResponseDto signup(@RequestBody SignupRequestDto requestDto) {
        return null;
    }

    // TODO: Pick this from header
    public UserDto validateToken(String token) {
        return null;
    }

    public void logout(@RequestBody LogoutRequestDto logoutRequestDto) {

    }


}

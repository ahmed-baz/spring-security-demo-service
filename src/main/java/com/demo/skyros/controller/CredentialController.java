package com.demo.skyros.controller;


import com.demo.skyros.exception.AppResponse;
import com.demo.skyros.security.service.AuthService;
import com.demo.skyros.security.vo.LoginRequestVO;
import com.demo.skyros.security.vo.TokenVO;
import com.demo.skyros.vo.AppUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequestMapping("auth/")
public class CredentialController {

    @Autowired
    private AuthService authService;

    @PostMapping("login")
    TokenVO login(@RequestBody LoginRequestVO requestVO) {
        return getAuthService().login(requestVO);
    }

    @PostMapping("refresh")
    TokenVO refreshAccessToken(@RequestBody TokenVO tokenVO) {
        return getAuthService().refreshAccessToken(tokenVO);
    }

    @PostMapping("register")
    AppResponse addUser(@RequestBody AppUserVO userVO) {
        return getAuthService().register(userVO);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

}

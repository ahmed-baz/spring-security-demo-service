package com.demo.skyros.controller;


import com.demo.skyros.exception.AppResponse;
import com.demo.skyros.service.AppUserService;
import com.demo.skyros.vo.AppUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@RestController
@RequestMapping("user")
public class AppUserController {

    @Autowired
    private AppUserService userService;

    @PutMapping("update")
    AppResponse updateUser(@RequestBody AppUserVO userVO) {
        return getUserService().updateUser(userVO);
    }

    @GetMapping("find/{id}")
    AppResponse findUserById(@PathVariable Long id) {
        return getUserService().findUserById(id);

    }

    public AppUserService getUserService() {
        return userService;
    }

    public void setUserService(AppUserService userService) {
        this.userService = userService;
    }
}

package com.demo.skyros.controller;


import com.demo.skyros.exception.AppResponse;
import com.demo.skyros.service.AppUserService;
import com.demo.skyros.vo.AppUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private AppUserService userService;

    @PostMapping("add")
    AppResponse addUser(@RequestBody AppUserVO userVO) {
        return getUserService().addUser(userVO);
    }

    @PutMapping("update")
    AppResponse updateUser(@RequestBody AppUserVO userVO) {
        return getUserService().updateUser(userVO);
    }

    @PutMapping("updateUserRole")
    AppResponse updateUserRoles(@RequestBody AppUserVO userVO) {
        return getUserService().updateUserRoles(userVO);
    }

    @GetMapping("find/{id}")
    AppResponse findUserById(@PathVariable Long id) {
        return getUserService().findUserById(id);
    }

    @DeleteMapping("delete/{id}")
    AppResponse deleteUser(@PathVariable Long id) {
        return getUserService().deleteUser(id);
    }

    @GetMapping("email/{email}")
    AppResponse findUserEmail(@PathVariable String email) {
        return getUserService().findByEmailOrUserName(email);
    }

    @GetMapping("find/all")
    AppResponse findUsers() {
        return getUserService().findUsers();
    }

    public AppUserService getUserService() {
        return userService;
    }

    public void setUserService(AppUserService userService) {
        this.userService = userService;
    }
}

package com.demo.skyros.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserVO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String password;
    private AuditVO audit;
    private Set<RoleVO> roles = new HashSet<>();
    private boolean isEnabled;
    private boolean isCredentialsNonExpired;
    private boolean isAccountNonLocked;
    private boolean isAccountNonExpired;

}



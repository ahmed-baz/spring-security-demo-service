package com.demo.skyros.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleVO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private AuditVO audit;

}



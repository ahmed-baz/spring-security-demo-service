package com.demo.skyros.mapper;

import com.demo.skyros.model.Role;
import com.demo.skyros.vo.RoleVO;
import org.mapstruct.Mapper;

@Mapper
public interface AppRoleMapper extends CommonMapper<Role, RoleVO> {

}

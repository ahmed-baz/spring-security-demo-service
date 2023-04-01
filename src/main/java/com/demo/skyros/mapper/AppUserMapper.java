package com.demo.skyros.mapper;

import com.demo.skyros.model.AppUser;
import com.demo.skyros.vo.AppUserVO;
import org.mapstruct.Mapper;

@Mapper
public interface AppUserMapper extends CommonMapper<AppUser, AppUserVO> {

}

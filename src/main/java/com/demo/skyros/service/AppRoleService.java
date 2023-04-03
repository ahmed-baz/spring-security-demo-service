package com.demo.skyros.service;


import com.demo.skyros.exception.AppResponse;
import com.demo.skyros.exception.RoleNotFoundException;
import com.demo.skyros.mapper.AppRoleMapper;
import com.demo.skyros.model.EntityAudit;
import com.demo.skyros.model.Role;
import com.demo.skyros.repo.AppRoleRepo;
import com.demo.skyros.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AppRoleService {

    @Autowired
    private AppRoleRepo appRoleRepo;
    @Autowired
    private AppRoleMapper appRoleMapper;

    public AppResponse addRole(RoleVO vo) {
        Role role = getAppRoleMapper().VOToEntity(vo);
        role.setId(null);
        role.setAudit(prepareSessionAudit());
        Role savedRole = getAppRoleRepo().save(role);
        RoleVO roleVO = getAppRoleMapper().entityToVO(savedRole);
        return prepareAppResponse(roleVO, null);
    }

    private EntityAudit prepareSessionAudit() {
        EntityAudit audit = new EntityAudit();
        audit.setCreatedBy("system");
        audit.setLastModifiedBy("system");
        audit.setCreatedDate(new Date());
        audit.setLastModifiedDate(new Date());
        return audit;
    }

    private AppResponse prepareAppResponse(Object data, String message) {
        AppResponse appResponse = new AppResponse(message);
        appResponse.setData(data);
        appResponse.setResponseDate(new Date());
        appResponse.setHttpStatus(HttpStatus.OK);
        return appResponse;
    }

    public AppResponse updateRole(RoleVO vo) {
        Role role = getAppRoleMapper().VOToEntity(vo);
        role.setAudit(prepareSessionAudit());
        Role savedRole = getAppRoleRepo().save(role);
        RoleVO roleVO = getAppRoleMapper().entityToVO(savedRole);
        return prepareAppResponse(roleVO, null);
    }

    public AppResponse findRoleById(Long id) {
        Role role = getAppRoleRepo().findById(id).orElse(null);
        if (null == role) {
            throw new RoleNotFoundException(id);
        }
        RoleVO roleVO = getAppRoleMapper().entityToVO(role);
        return prepareAppResponse(roleVO, null);
    }

    public AppResponse deleteUser(Long id) {
        try {
            getAppRoleRepo().deleteById(id);
        } catch (EmptyResultDataAccessException exception) {
            throw new RoleNotFoundException(id);
        }
        return prepareAppResponse(null, "role deleted");
    }

    public AppResponse findByCode(String code) {
        Role role = getAppRoleRepo().findByCode(code);
        if (null == role) {
            throw new RoleNotFoundException(code);
        }
        RoleVO roleVO = getAppRoleMapper().entityToVO(role);
        return prepareAppResponse(roleVO, null);
    }

    @Cacheable(value = "getUserRole")
    public Role getUserRole() {
        Role role = getAppRoleRepo().findByCode("USER");
        return role;
    }

    public AppResponse findRoles() {
        List<Role> roles = getAppRoleRepo().findAll();
        List<RoleVO> roleVOS = getAppRoleMapper().entityListToVOList(roles);
        return prepareAppResponse(roleVOS, null);
    }

    public AppRoleRepo getAppRoleRepo() {
        return appRoleRepo;
    }

    public void setAppRoleRepo(AppRoleRepo appRoleRepo) {
        this.appRoleRepo = appRoleRepo;
    }

    public AppRoleMapper getAppRoleMapper() {
        return appRoleMapper;
    }

    public void setAppRoleMapper(AppRoleMapper appRoleMapper) {
        this.appRoleMapper = appRoleMapper;
    }
}

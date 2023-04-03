package com.demo.skyros.service;


import com.demo.skyros.exception.AppResponse;
import com.demo.skyros.exception.NoneUniqueResultException;
import com.demo.skyros.exception.UserNotFoundException;
import com.demo.skyros.mapper.AppUserMapper;
import com.demo.skyros.model.AppUser;
import com.demo.skyros.model.EntityAudit;
import com.demo.skyros.model.Role;
import com.demo.skyros.model.UserRole;
import com.demo.skyros.repo.AppUserRepo;
import com.demo.skyros.repo.UserRoleRepo;
import com.demo.skyros.vo.AppUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepo userRepo;
    @Autowired
    private AppUserMapper userMapper;
    @Autowired
    private AppRoleService appRoleService;
    @Autowired
    private UserRoleRepo userRoleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AppResponse addUser(AppUserVO userVO) {
        //1. map vo into entity
        AppUser appUser = getUserMapper().VOToEntity(userVO);

        //2. override user Id and roles
        appUser.setId(null);
        Set<Role> roles = appUser.getRoles();
        appUser.setRoles(new HashSet<>());

        //3. encode password
        appUser.setPassword(getPasswordEncoder().encode(appUser.getPassword()));

        //4. prepare audit
        appUser.setAudit(prepareSessionAudit());

        //5. save user
        AppUser savedUser = saveUser(appUser);

        //6. save user roles
        saveUserRoles(savedUser, roles);

        //7. set user role
        savedUser.setRoles(roles);

        //8. map entity into vo
        AppUserVO appUserVO = getUserMapper().entityToVO(savedUser);

        return prepareAppResponse(appUserVO, null);
    }

    private void saveUserRoles(AppUser savedUser, Set<Role> roles) {
        List<UserRole> userRoles = new ArrayList<>();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            UserRole userRole = new UserRole();
            userRole.setAppUser(savedUser);
            Role role = getAppRoleService().getUserRole();
            userRole.setRole(role);
            userRoles.add(userRole);
        } else {
            roles.forEach(role -> {
                UserRole userRole = new UserRole();
                userRole.setRole(role);
                userRole.setAppUser(savedUser);
                userRoles.add(userRole);
            });
        }
        getUserRoleRepo().saveAll(userRoles);
    }

    public AppUser saveUser(AppUser user) {
        try {
            user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));
            return getUserRepo().save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new NoneUniqueResultException("email");
        } catch (Exception ex) {
            throw ex;
        }
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

    public AppResponse updateUser(AppUserVO userVO) {
        AppUser appUser = getUserMapper().VOToEntity(userVO);
        AppUser savedUser = getUserRepo().save(appUser);
        AppUserVO appUserVO = getUserMapper().entityToVO(savedUser);
        return prepareAppResponse(appUserVO, null);
    }

    @Transactional
    public AppResponse updateUserRoles(AppUserVO userVO) {
        AppUser appUser = getUserMapper().VOToEntity(userVO);
        Set<Role> roles = appUser.getRoles();
        getUserRoleRepo().deleteAllByAppUser(appUser);
        saveUserRoles(appUser, roles);
        return prepareAppResponse(userVO, null);
    }

    @Cacheable(value = "findUserById", key = "#id")
    public AppResponse findUserById(Long id) {
        AppUser appUser = getUserRepo().findById(id).orElse(null);
        if (null == appUser) {
            throw new UserNotFoundException(id);
        }
        AppUserVO appUserVO = getUserMapper().entityToVO(appUser);
        return prepareAppResponse(appUserVO, null);
    }

    public AppResponse deleteUser(Long id) {
        try {
            getUserRepo().deleteById(id);
        } catch (EmptyResultDataAccessException exception) {
            throw new UserNotFoundException(id);
        }
        return prepareAppResponse(null, "user deleted");
    }

    @Cacheable(value = "findUserByEmailOrUserName", key = "#key")
    public AppResponse findByEmailOrUserName(String key) {
        AppUser appUser = getUserRepo().findByEmailOrUserName(key, key);
        if (null == appUser) {
            throw new UserNotFoundException(key);
        }
        AppUserVO appUserVO = getUserMapper().entityToVO(appUser);
        return prepareAppResponse(appUserVO, null);
    }

    public AppResponse findUsers() {
        List<AppUser> appUser = getUserRepo().findAll();
        List<AppUserVO> appUserVOS = getUserMapper().entityListToVOList(appUser);
        return prepareAppResponse(appUserVOS, null);
    }

    public AppUserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(AppUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public AppUserMapper getUserMapper() {
        return userMapper;
    }

    public void setUserMapper(AppUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public AppRoleService getAppRoleService() {
        return appRoleService;
    }

    public void setAppRoleService(AppRoleService appRoleService) {
        this.appRoleService = appRoleService;
    }

    public UserRoleRepo getUserRoleRepo() {
        return userRoleRepo;
    }

    public void setUserRoleRepo(UserRoleRepo userRoleRepo) {
        this.userRoleRepo = userRoleRepo;
    }
}

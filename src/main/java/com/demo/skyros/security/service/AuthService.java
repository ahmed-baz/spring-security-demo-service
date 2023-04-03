package com.demo.skyros.security.service;


import com.demo.skyros.exception.AppResponse;
import com.demo.skyros.exception.TokenExpiredException;
import com.demo.skyros.mapper.AppUserMapper;
import com.demo.skyros.model.AppUser;
import com.demo.skyros.model.EntityAudit;
import com.demo.skyros.model.Role;
import com.demo.skyros.model.UserRole;
import com.demo.skyros.repo.AppUserRepo;
import com.demo.skyros.repo.UserRoleRepo;
import com.demo.skyros.security.model.TokenInfo;
import com.demo.skyros.security.repo.TokenInfoRepo;
import com.demo.skyros.security.vo.AppUserDetails;
import com.demo.skyros.security.vo.LoginRequestVO;
import com.demo.skyros.security.vo.TokenVO;
import com.demo.skyros.service.AppRoleService;
import com.demo.skyros.service.AppUserService;
import com.demo.skyros.util.AppUtil;
import com.demo.skyros.vo.AppUserVO;
import com.demo.skyros.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final HttpServletRequest httpRequest;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenInfoRepo tokenInfoRepo;
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
    @Autowired
    private AppUtil appUtil;

    @Cacheable(value = "loadUserByUsername", key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppResponse appResponse = getAppUserService().findByEmailOrUserName(username);
        AppUserVO userVO = (AppUserVO) appResponse.getData();
        AppUserDetails appUserDetails = new AppUserDetails(userVO);
        //User user = new User(userVO.getUserName(), userVO.getPassword(), getUserGrantedAuthority(userVO.getRoles()));
        return appUserDetails;
    }

    private List<GrantedAuthority> getUserGrantedAuthority(Set<RoleVO> roles) {
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        roles.forEach(roleVO -> {
            grantedAuthorityList.add(new SimpleGrantedAuthority(roleVO.getCode()));
        });
        return grantedAuthorityList;
    }

    public TokenVO login(LoginRequestVO requestVO) {
        String userName = requestVO.getUserName();
        Authentication authentication = getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userName, requestVO.getPassword()));
        AppUserDetails appUserDetails = (AppUserDetails) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TokenVO responseVO = generateUserTokens(userName, appUserDetails.getId());
        return responseVO;
    }

    public AppResponse register(AppUserVO userVO) {
        //1. map vo into entity
        AppUser appUser = getUserMapper().VOToEntity(userVO);

        //2. override user Id and roles
        appUser.setId(null);
        appUser.setRoles(new HashSet<>());

        //3. encode password
        appUser.setPassword(getPasswordEncoder().encode(appUser.getPassword()));

        //4. prepare audit
        appUser.setAudit(prepareSessionAudit());

        //5. save user
        AppUser savedUser = getAppUserService().saveUser(appUser);

        //6. save user roles
        Set<Role> roles = new HashSet<>();
        UserRole userRole = new UserRole();
        userRole.setAppUser(savedUser);
        Role role = getAppRoleService().getUserRole();
        userRole.setRole(role);
        getUserRoleRepo().save(userRole);
        roles.add(role);

        //7. set user role
        savedUser.setRoles(roles);

        //8. map entity into vo
        AppUserVO appUserVO = getUserMapper().entityToVO(savedUser);

        //9. generate OTP
        //String otp = getAppUtil().generateUserOTP(appUserVO.getEmail(), appUserVO.getId(), OTPTypeEnum.REGISTER_ACTIVATION);

        //10. send OTP
        //sendOTP(appUserVO.getEmail(), otp);

        return prepareAppResponse(appUserVO, null);
    }

    private String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e);
            return null;
        }
    }

    private AppResponse prepareAppResponse(Object data, String message) {
        AppResponse appResponse = new AppResponse(message);
        appResponse.setData(data);
        appResponse.setResponseDate(new Date());
        appResponse.setHttpStatus(HttpStatus.OK);
        return appResponse;
    }

    public TokenVO generateUserTokens(String userName, Long userId) {
        String userAgent = httpRequest.getHeader(HttpHeaders.USER_AGENT);
        String hostAddress = getLocalHost();
        String accessTokenId = UUID.randomUUID().toString();
        String accessToken = getJwtTokenUtil().generateToken(userName, accessTokenId, false);
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshToken = getJwtTokenUtil().generateToken(userName, refreshTokenId, true);
        TokenInfo tokenInfo = new TokenInfo(accessToken, refreshToken);
        tokenInfo.setAppUser(new AppUser(userId));
        tokenInfo.setAgent(userAgent);
        tokenInfo.setLocalIpAddress(hostAddress);
        tokenInfo.setRemoteIpAddress(httpRequest.getRemoteAddr());
        tokenInfo.setAudit(prepareSessionAudit());
        //TokenInfo savedTokenInfo = getTokenInfoRepo().save(tokenInfo);
        TokenVO responseVO = new TokenVO();
        responseVO.setAccessToken(tokenInfo.getAccessToken());
        responseVO.setRefreshToken(tokenInfo.getRefreshToken());
        responseVO.setUserName(userName);
        return responseVO;
    }

    public void sendOTP(String userName, String otp) {

    }

    public TokenVO refreshAccessToken(TokenVO tokenVO) {
        String refreshToken = tokenVO.getRefreshToken();
        if (getJwtTokenUtil().isTokenExpired(refreshToken)) {
            throw new TokenExpiredException("refresh");
        }
        String userName = getJwtTokenUtil().getUserNameFromToken(refreshToken);
        /*
         TokenInfo tokenInfo = getTokenInfoRepo().findByRefreshToken(refreshToken);

         if (null == tokenInfo) {
             throw new TokenNotFoundException();
         }
        */

        String accessToken = getJwtTokenUtil().generateToken(userName, UUID.randomUUID().toString(), false);

        //tokenInfo.setAccessToken(accessToken);
        //tokenInfo.setAudit(prepareSessionAudit());
        //getTokenInfoRepo().save(tokenInfo);

        TokenVO responseVO = new TokenVO(userName, accessToken, refreshToken);
        return responseVO;

    }

    public void logout(TokenVO tokenVO) {

    }


    private EntityAudit prepareSessionAudit() {
        EntityAudit audit = new EntityAudit();
        audit.setCreatedBy("system");
        audit.setLastModifiedBy("system");
        audit.setCreatedDate(new Date());
        audit.setLastModifiedDate(new Date());
        return audit;
    }


    public AppUserService getAppUserService() {
        return appUserService;
    }

    public void setAppUserService(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    public JwtTokenUtil getJwtTokenUtil() {
        return jwtTokenUtil;
    }

    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    public TokenInfoRepo getTokenInfoRepo() {
        return tokenInfoRepo;
    }

    public void setTokenInfoRepo(TokenInfoRepo tokenInfoRepo) {
        this.tokenInfoRepo = tokenInfoRepo;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
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

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public AppUtil getAppUtil() {
        return appUtil;
    }

    public void setAppUtil(AppUtil appUtil) {
        this.appUtil = appUtil;
    }
}


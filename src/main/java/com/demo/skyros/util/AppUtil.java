package com.demo.skyros.util;

import com.demo.skyros.model.AppUser;
import com.demo.skyros.model.EntityAudit;
import com.demo.skyros.model.UserOTP;
import com.demo.skyros.repo.AppUserRepo;
import com.demo.skyros.repo.UserOTPRepo;
import com.demo.skyros.security.exception.InvalidOtpException;
import com.demo.skyros.security.vo.LoginRequestVO;
import com.demo.skyros.security.vo.enums.LoginStatusEnum;
import com.demo.skyros.security.vo.enums.OTPTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

@Service
public class AppUtil {

    @Value("${register.otp.expiration}")
    private long registerExpiration;
    @Value("${forget.password.otp.expiration}")
    private long forgetPasswordExpiration;
    @Autowired
    private UserOTPRepo userOTPRepo;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private AppUserRepo appUserRepo;

    public String generateUserOTP(String userName, OTPTypeEnum otpTypeEnum) {
        String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
        UserOTP userOTP = new UserOTP();
        userOTP.setOtpValue(otp);
        userOTP.setOtpTypeEnum(otpTypeEnum);
        userOTP.setUserName(userName);
        userOTP.setExpired(false);
        userOTP.setDamaged(false);
        userOTP.setConsumed(false);
        userOTP.setExpirationDate(getOTPExpirationDate(otpTypeEnum));
        userOTP.setAudit(prepareSessionAudit());
        return getUserOTPRepo().save(userOTP).getOtpValue();
    }

    public void validateUserOTP(LoginRequestVO requestVO) {
        checkValidUserOTP(requestVO);

    }

    private void checkValidUserOTP(LoginRequestVO requestVO) {
        try {
            String email = requestVO.getUserName();
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserOTP> criteriaQuery = criteriaBuilder.createQuery(UserOTP.class);
            Root<UserOTP> itemRoot = criteriaQuery.from(UserOTP.class);
            Predicate predicateUserName
                    = criteriaBuilder.equal(itemRoot.get("userName"), email);
            Predicate predicateOTP
                    = criteriaBuilder.equal(itemRoot.get("otpValue"), requestVO.getOtp());
            Predicate predicateOTPType
                    = criteriaBuilder.equal(itemRoot.get("otpTypeEnum"), OTPTypeEnum.REGISTER_ACTIVATION);
            Predicate predicateIsExpired
                    = criteriaBuilder.equal(itemRoot.get("isExpired"), 0);
            Predicate predicateIsDamaged
                    = criteriaBuilder.equal(itemRoot.get("isDamaged"), 0);
            Predicate predicateIsConsumed
                    = criteriaBuilder.equal(itemRoot.get("isConsumed"), 0);
            Predicate predicateExpirationDate
                    = criteriaBuilder.greaterThan(itemRoot.get("expirationDate"), new Date());
            Predicate predicate
                    = criteriaBuilder.and(predicateUserName, predicateOTP, predicateOTPType, predicateIsExpired, predicateIsDamaged, predicateIsConsumed, predicateExpirationDate);
            criteriaQuery.where(predicate);
            UserOTP userOTP = getEntityManager().createQuery(criteriaQuery).getSingleResult();

            AppUser appUser = getAppUserRepo().findByEmail(email);
            appUser.setLoginStatusEnum(LoginStatusEnum.ACTIVE);
            getAppUserRepo().save(appUser);

            userOTP.setConsumed(true);
            getUserOTPRepo().save(userOTP);

        } catch (NoResultException ex) {
            throw new InvalidOtpException();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Date getOTPExpirationDate(OTPTypeEnum otpTypeEnum) {
        switch (otpTypeEnum) {
            case REGISTER_ACTIVATION:
                return new Date((System.currentTimeMillis() + getRegisterExpiration()) * 1000);
            case FORGET_PASSWORD:
                return new Date((System.currentTimeMillis() + getForgetPasswordExpiration()) * 1000);
        }
        throw new UnsupportedOperationException();
    }

    private EntityAudit prepareSessionAudit() {
        EntityAudit audit = new EntityAudit();
        audit.setCreatedBy("system");
        audit.setLastModifiedBy("system");
        audit.setCreatedDate(new Date());
        audit.setLastModifiedDate(new Date());
        return audit;
    }

    public long getRegisterExpiration() {
        return registerExpiration;
    }

    public void setRegisterExpiration(long registerExpiration) {
        this.registerExpiration = registerExpiration;
    }

    public UserOTPRepo getUserOTPRepo() {
        return userOTPRepo;
    }

    public void setUserOTPRepo(UserOTPRepo userOTPRepo) {
        this.userOTPRepo = userOTPRepo;
    }

    public long getForgetPasswordExpiration() {
        return forgetPasswordExpiration;
    }

    public void setForgetPasswordExpiration(long forgetPasswordExpiration) {
        this.forgetPasswordExpiration = forgetPasswordExpiration;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public AppUserRepo getAppUserRepo() {
        return appUserRepo;
    }

    public void setAppUserRepo(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }
}

package com.demo.skyros.util;

import com.demo.skyros.model.EntityAudit;
import com.demo.skyros.model.UserOTP;
import com.demo.skyros.repo.UserOTPRepo;
import com.demo.skyros.security.vo.OTPTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
}

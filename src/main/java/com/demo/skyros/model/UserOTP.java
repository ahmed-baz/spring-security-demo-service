package com.demo.skyros.model;

import com.demo.skyros.security.vo.OTPTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USER_OTP")
public class UserOTP extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_OTP_SEQ")
    @SequenceGenerator(name = "USER_OTP_SEQ", sequenceName = "USER_OTP_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "OTP_VALUE")
    private String otpValue;
    @Column(name = "OTP_TYPE")
    @Enumerated(EnumType.STRING)
    private OTPTypeEnum otpTypeEnum;
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "IS_DAMAGED")
    private boolean isDamaged;
    @Column(name = "IS_EXPIRED")
    private boolean isExpired;
    @Column(name = "IS_CONSUMED")
    private boolean isConsumed;
    @Column(name = "EXPIRATION_DATE")
    private Date expirationDate;

}



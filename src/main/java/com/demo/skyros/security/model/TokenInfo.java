package com.demo.skyros.security.model;

import com.demo.skyros.model.AppUser;
import com.demo.skyros.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TOKEN_INFO")
public class TokenInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TOKEN_INFO_SEQ")
    @SequenceGenerator(name = "TOKEN_INFO_SEQ", sequenceName = "TOKEN_INFO_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @NotBlank
    @Column(name = "ACCESS_TOKEN")
    private String accessToken;
    @NotBlank
    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;
    @Column(name = "AGENT")
    private String agent;
    @Column(name = "LOCAL_IP_ADDRESS")
    private String localIpAddress;
    @Column(name = "REMOTE_IP_ADDRESS")
    private String remoteIpAddress;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private AppUser appUser;

    public TokenInfo(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}



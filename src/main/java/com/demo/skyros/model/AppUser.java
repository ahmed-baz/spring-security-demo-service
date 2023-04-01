package com.demo.skyros.model;

import com.demo.skyros.security.model.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "APP_USER")
public class AppUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_USER_SEQ")
    @SequenceGenerator(name = "APP_USER_SEQ", sequenceName = "APP_USER_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "PASSWORD")
    private String password;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    @OrderColumn(name = "ID")
    private Set<Role> roles = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL)
    private Set<TokenInfo> tokenInfos = new HashSet<>();
    @Column(name = "IS_ENABLED")
    private boolean isEnabled;
    @Column(name = "IS_CREDENTIALS_NON_EXPIRED")
    private boolean isCredentialsNonExpired;
    @Column(name = "IS_ACCOUNT_NON_LOCKED")
    private boolean isAccountNonLocked;
    @Column(name = "IS_ACCOUNT_NON_EXPIRED")
    private boolean isAccountNonExpired;

    public AppUser(Long id) {
        this.id = id;
    }
}



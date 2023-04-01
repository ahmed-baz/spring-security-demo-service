package com.demo.skyros.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USER_ROLE")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ROLE_SEQ")
    @SequenceGenerator(name = "USER_ROLE_SEQ", sequenceName = "USER_ROLE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private AppUser appUser;
    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    private Role role;

}



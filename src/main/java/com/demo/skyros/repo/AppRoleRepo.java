package com.demo.skyros.repo;

import com.demo.skyros.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRoleRepo extends JpaRepository<Role, Long> {

    Role findByCode(String code);

}

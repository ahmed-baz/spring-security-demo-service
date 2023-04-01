package com.demo.skyros.repo;

import com.demo.skyros.model.AppUser;
import com.demo.skyros.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, Long> {

    void deleteAllByAppUser(AppUser appUser);

}

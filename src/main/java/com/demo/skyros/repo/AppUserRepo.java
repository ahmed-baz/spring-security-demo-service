package com.demo.skyros.repo;

import com.demo.skyros.model.AppUser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Long> {

    AppUser findByEmailOrUserName(String email, String userName);

}

package com.demo.skyros.repo;

import com.demo.skyros.model.UserOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOTPRepo extends JpaRepository<UserOTP, Long> {

}

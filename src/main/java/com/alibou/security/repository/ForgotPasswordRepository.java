package com.alibou.security.repository;

import com.alibou.security.entity.ForgotPassword;
import com.alibou.security.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {
    @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.user = ?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);

    ForgotPassword findByUser(User user);

    @Modifying
    @Transactional
    @Query("delete from ForgotPassword where fpId=?1")
    void deleteByFpId(Integer fpId);
}

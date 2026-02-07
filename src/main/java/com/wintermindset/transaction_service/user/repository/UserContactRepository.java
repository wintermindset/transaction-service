package com.wintermindset.transaction_service.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wintermindset.transaction_service.user.entity.UserContactEntity;

@Repository
public interface UserContactRepository extends JpaRepository<UserContactEntity, Long> {
    
    boolean existsByValue(String value);
}
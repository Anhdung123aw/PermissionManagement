package com.example.PermissionManagement.repository;

import com.example.PermissionManagement.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    boolean existsByUserName(String userName);
    Optional<UserEntity> findByUserName(String userName);
}

package com.example.PermissionManagement.repository;

import com.example.PermissionManagement.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity,String> {
}

package com.example.PermissionManagement.repository;

import com.example.PermissionManagement.entity.EndpointPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EndpointPermissionRepository extends JpaRepository<EndpointPermissionEntity,Long> {
    List<EndpointPermissionEntity> findByHttpMethod(String httpMethod);
}

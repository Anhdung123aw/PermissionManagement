package com.example.PermissionManagement.repository;

import com.example.PermissionManagement.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,String> {
    boolean existsByRoleName(String roleName);

}

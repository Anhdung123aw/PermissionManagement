package com.example.PermissionManagement.service;

import com.example.PermissionManagement.dto.request.RoleRequest;
import com.example.PermissionManagement.dto.response.RoleResponse;
import com.example.PermissionManagement.entity.RoleEntity;
import com.example.PermissionManagement.repository.PermissionRepository;
import com.example.PermissionManagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;


    public RoleResponse createRole(RoleRequest request) {
        RoleEntity role = new RoleEntity();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        // Lấy danh sách PermissionEntity từ database gán vào Role
        if (request.getPermissions() != null) {
            var permissions = permissionRepository.findAllById(request.getPermissions());
            role.setPermissions(new HashSet<>(permissions));
        }
        role = roleRepository.save(role);
        return new RoleResponse(role);
    }

    public List<RoleResponse> getRoles() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::new)
                .toList();
    }
    public void deleteRole(String roleCode) {
        roleRepository.deleteById(roleCode);
    }
}

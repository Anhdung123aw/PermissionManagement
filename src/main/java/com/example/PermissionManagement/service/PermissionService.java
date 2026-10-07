package com.example.PermissionManagement.service;

import com.example.PermissionManagement.dto.request.PermissionRequest;
import com.example.PermissionManagement.dto.response.PermissionResponse;
import com.example.PermissionManagement.entity.PermissionEntity;
import com.example.PermissionManagement.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionResponse createPermission(PermissionRequest request){
        PermissionEntity entity = new PermissionEntity();
        entity.setPermissionCode(request.getPermissionCode());
        entity.setPermissionName(request.getPermissionName());
        entity.setDescription(request.getDescription());
        entity = permissionRepository.save(entity);
        return new PermissionResponse(entity);
    }
    public List<PermissionResponse> getPermissions() {
        return permissionRepository.findAll().stream()
                .map(PermissionResponse::new)
                .toList();
    }
    public void deletePermission(String permissionCode) {
        permissionRepository.deleteById(permissionCode);
    }
}

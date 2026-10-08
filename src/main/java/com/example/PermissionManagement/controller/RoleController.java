package com.example.PermissionManagement.controller;

import com.example.PermissionManagement.dto.request.ApiResponse;
import com.example.PermissionManagement.dto.request.RoleRequest;
import com.example.PermissionManagement.dto.response.RoleResponse;
import com.example.PermissionManagement.service.RoleService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(request))
                .build();
    }
    @GetMapping
    public ApiResponse<List<RoleResponse>> getRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getRoles())
                .build();
    }
    @DeleteMapping("/{roleCode}")
    public ApiResponse<String> deleteRole(@PathVariable String roleCode) {
        roleService.deleteRole(roleCode);
        return ApiResponse.<String>builder()
                .result("Role has been deleted")
                .build();
    }
}

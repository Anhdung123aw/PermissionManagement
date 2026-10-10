package com.example.PermissionManagement.controller;

import com.example.PermissionManagement.dto.request.ApiResponse;
import com.example.PermissionManagement.dto.request.RoleFilterRequest;
import com.example.PermissionManagement.dto.request.RoleRequest;
import com.example.PermissionManagement.dto.request.SearchRequest;
import com.example.PermissionManagement.dto.response.RoleResponse;
import com.example.PermissionManagement.dto.response.SearchResponse;
import com.example.PermissionManagement.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/search")
    public ApiResponse<SearchResponse<RoleResponse>> searchRoles(@RequestBody SearchRequest<RoleFilterRequest> request) {
        return ApiResponse.<SearchResponse<RoleResponse>>builder()
                .result(roleService.searchRoles(request))
                .build();
    }
}

package com.example.PermissionManagement.controller;

import com.example.PermissionManagement.dto.request.ApiResponse;
import com.example.PermissionManagement.dto.request.PermissionFilterRequest;
import com.example.PermissionManagement.dto.request.PermissionRequest;
import com.example.PermissionManagement.dto.request.SearchRequest;
import com.example.PermissionManagement.dto.response.PermissionResponse;
import com.example.PermissionManagement.dto.response.SearchResponse;
import com.example.PermissionManagement.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request){
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getPermissions() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getPermissions())
                .build();
    }

    @DeleteMapping("/{permissionCode}")
    public ApiResponse<String> deletePermission(@PathVariable String permissionCode) {
        permissionService.deletePermission(permissionCode);
        return ApiResponse.<String>builder()
                .result("Permission has been deleted")
                .build();
    }

    @PostMapping("/search")
    public ApiResponse<SearchResponse<PermissionResponse>> searchPermissions(@RequestBody SearchRequest<PermissionFilterRequest> request) {
        return ApiResponse.<SearchResponse<PermissionResponse>>builder()
                .result(permissionService.searchPermissions(request))
                .build();
    }
}

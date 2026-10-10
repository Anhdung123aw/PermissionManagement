package com.example.PermissionManagement.dto.request;

import lombok.Data;

@Data
public class RoleFilterRequest {
    private String keyword; // Tìm theo mã vai trò, tên vai trò hoặc mô tả
}

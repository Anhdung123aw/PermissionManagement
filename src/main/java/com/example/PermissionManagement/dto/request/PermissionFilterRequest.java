package com.example.PermissionManagement.dto.request;

import lombok.Data;

@Data
public class PermissionFilterRequest {
    private String keyword; // Tìm theo mã quyền, tên quyền hoặc mô tả
}

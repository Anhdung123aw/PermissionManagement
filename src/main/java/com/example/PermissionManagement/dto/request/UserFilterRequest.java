package com.example.PermissionManagement.dto.request;

import lombok.Data;

@Data
public class UserFilterRequest {
    private String keyword;  // Tìm theo username hoặc email
}
package com.example.PermissionManagement.dto.response;

import com.example.PermissionManagement.entity.PermissionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    private String permissionCode;
    private String permissionName;
    private String description;

    public PermissionResponse(PermissionEntity entity){
        this.permissionCode=entity.getPermissionCode();
        this.permissionName=entity.getPermissionName();
        this.description=entity.getDescription();
    }
}

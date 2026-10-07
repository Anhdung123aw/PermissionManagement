package com.example.PermissionManagement.dto.response;


import com.example.PermissionManagement.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;
import java.util.Set;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private String roleCode;
    private String roleName;
    private String description;
    private Set<PermissionResponse> permissions;

    public RoleResponse(RoleEntity entity){
        this.roleCode=entity.getRoleCode();
        this.roleName=entity.getRoleName();
        this.description=entity.getDescription();
        if(entity.getPermissions()!=null){
            this.permissions=entity.getPermissions().stream()
                    .map(PermissionResponse::new)
                    .collect(Collectors.toSet());
        }
    }
}

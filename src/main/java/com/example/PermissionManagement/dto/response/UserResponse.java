package com.example.PermissionManagement.dto.response;

import com.example.PermissionManagement.entity.RoleEntity;
import com.example.PermissionManagement.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserResponse {
    private String id;
    private String userName;
    Set<RoleEntity> roles;
    public UserResponse(UserEntity entity){
        this.id = String.valueOf(entity.getId());
        this.userName=entity.getUserName();
        this.roles=entity.getRoles();

    }
}

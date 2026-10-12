package com.example.PermissionManagement.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
        private Long id;
        private String userName;
        private String email;
        private Set<String> roles;
        private Set<String> permissions;
}

package com.example.PermissionManagement.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.Permission;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="ROLES")
public class RoleEntity {
    @Id
    @Column(name="ROLE_CODE")
    private String roleCode;

    @Column(name="ROLE_NAME")
    private String roleName;

    @Column(name="DESCRIPTION")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ROLE_PERMISSIONS",
            joinColumns = @JoinColumn(name = "ROLE_CODE",referencedColumnName = "ROLE_CODE"),
            inverseJoinColumns = @JoinColumn(name = "PERMISSION_CODE",referencedColumnName = "PERMISSION_CODE")
    )
    private Set<PermissionEntity> permissions = new HashSet<>();
}

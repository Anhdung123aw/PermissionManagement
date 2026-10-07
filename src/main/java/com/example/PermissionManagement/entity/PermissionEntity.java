package com.example.PermissionManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="PERMISSIONS")
public class PermissionEntity {
    @Id
    @Column(name="PERMISSION_CODE")
    private String permissionCode;

    @Column(name="PERMISSION_NAME")
    private String permissionName;

    @Column(name="DESCRIPTION")
    private String description;

}

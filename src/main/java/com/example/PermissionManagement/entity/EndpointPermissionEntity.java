package com.example.PermissionManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Table(name="ENDPOINT_PERMISSIONS")
@Getter
@Setter
@Builder
public class EndpointPermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Column(name="NAME")
    private String name;

    @Column(name = "HTTP_METHOD")
    private String httpMethod;

    @Column(name="URL_PATTERN")
    private String urlPattern;

    @Column(name="PERMISSION_CODE")
    private String permissionCode;

    @Column(name="DESCRIPTION")
    private String description;

}


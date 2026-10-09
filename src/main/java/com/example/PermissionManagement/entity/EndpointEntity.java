package com.example.PermissionManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Entity
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ENDPOINTS")
@Getter
@Setter
@Builder
public class EndpointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "HTTP_METHOD")
    private String httpMethod;

    @Column(name = "URL_PATTERN")
    private String urlPattern;

    @Column(name = "DESCRIPTION")
    private String description;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "ENDPOINT_PERMISSIONS",
            joinColumns = @JoinColumn(name = "ENDPOINT_ID", referencedColumnName = "ID")
    )
    @Column(name = "PERMISSION_CODE")
    private Set<String> permissions = new HashSet<>();
}
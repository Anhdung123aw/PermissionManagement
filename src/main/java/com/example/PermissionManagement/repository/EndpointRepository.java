package com.example.PermissionManagement.repository;

import com.example.PermissionManagement.entity.EndpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndpointRepository extends JpaRepository<EndpointEntity, Long> {
    @Query("SELECT DISTINCT e FROM EndpointEntity e LEFT JOIN FETCH e.permissions WHERE e.httpMethod = :httpMethod")
    List<EndpointEntity> findByHttpMethod(@Param("httpMethod") String httpMethod);

    List<EndpointEntity> findByUrlPattern(String urlPattern);
}
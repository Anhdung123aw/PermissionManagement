package com.example.PermissionManagement.service;

import com.example.PermissionManagement.Utils.PageableUtils;
import com.example.PermissionManagement.dto.request.RoleFilterRequest;
import com.example.PermissionManagement.dto.request.RoleRequest;
import com.example.PermissionManagement.dto.request.SearchRequest;
import com.example.PermissionManagement.dto.response.RoleResponse;
import com.example.PermissionManagement.dto.response.SearchResponse;
import com.example.PermissionManagement.entity.RoleEntity;
import com.example.PermissionManagement.exception.AppException;
import com.example.PermissionManagement.exception.ErrorCode;
import com.example.PermissionManagement.repository.PermissionRepository;
import com.example.PermissionManagement.repository.RoleRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleResponse createRole(RoleRequest request) {
        RoleEntity role = new RoleEntity();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        if (request.getPermissions() != null) {
            var permissions = permissionRepository.findAllById(request.getPermissions());
            role.setPermissions(new HashSet<>(permissions));
        }
        role = roleRepository.save(role);
        return new RoleResponse(role);
    }

    public List<RoleResponse> getRoles() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::new)
                .toList();
    }

    public void deleteRole(String roleCode) {
        roleRepository.deleteById(roleCode);
    }

    public void validateSearch(SearchRequest<?> request) {
        if (request == null || request.getPagination() == null) {
            throw new AppException(ErrorCode.INVALID_PAGINATION);
        }
        int page = request.getPagination().getPage();
        int size = request.getPagination().getSize();
        if (page < 1 || size < 1 || size > 100) {
            throw new AppException(ErrorCode.INVALID_PAGINATION);
        }
    }

    @Transactional(readOnly = true)
    public SearchResponse<RoleResponse> searchRoles(SearchRequest<RoleFilterRequest> request) {
        validateSearch(request);
        Pageable pageable = PageableUtils.getPageable(request, "roleCode");

        RoleFilterRequest filter = request.getFilter();
        Specification<RoleEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter != null && StringUtils.hasText(filter.getKeyword())) {
                String kw = "%" + filter.getKeyword().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("roleCode")), kw),
                        cb.like(cb.lower(root.get("roleName")), kw),
                        cb.like(cb.lower(cb.coalesce(root.get("description"), "")), kw)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<RoleEntity> pageResult = roleRepository.findAll(spec, pageable);
        List<RoleResponse> data = pageResult.getContent().stream().map(RoleResponse::new).toList();
        return new SearchResponse<>(data, pageResult.getTotalElements());
    }
}

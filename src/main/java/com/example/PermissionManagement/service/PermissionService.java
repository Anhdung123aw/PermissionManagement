package com.example.PermissionManagement.service;

import com.example.PermissionManagement.Utils.PageableUtils;
import com.example.PermissionManagement.dto.request.PermissionFilterRequest;
import com.example.PermissionManagement.dto.request.PermissionRequest;
import com.example.PermissionManagement.dto.request.SearchRequest;
import com.example.PermissionManagement.dto.response.PermissionResponse;
import com.example.PermissionManagement.dto.response.SearchResponse;
import com.example.PermissionManagement.entity.PermissionEntity;
import com.example.PermissionManagement.exception.AppException;
import com.example.PermissionManagement.exception.ErrorCode;
import com.example.PermissionManagement.repository.PermissionRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionResponse createPermission(PermissionRequest request){
        PermissionEntity entity = new PermissionEntity();
        entity.setPermissionCode(request.getPermissionCode());
        entity.setPermissionName(request.getPermissionName());
        entity.setDescription(request.getDescription());
        entity = permissionRepository.save(entity);
        return new PermissionResponse(entity);
    }

    public List<PermissionResponse> getPermissions() {
        return permissionRepository.findAll().stream()
                .map(PermissionResponse::new)
                .toList();
    }

    public void deletePermission(String permissionCode) {
        permissionRepository.deleteById(permissionCode);
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
    public SearchResponse<PermissionResponse> searchPermissions(SearchRequest<PermissionFilterRequest> request) {
        validateSearch(request);
        Pageable pageable = PageableUtils.getPageable(request, "permissionCode");

        PermissionFilterRequest filter = request.getFilter();
        Specification<PermissionEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter != null && StringUtils.hasText(filter.getKeyword())) {
                String kw = "%" + filter.getKeyword().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("permissionCode")), kw),
                        cb.like(cb.lower(root.get("permissionName")), kw),
                        cb.like(cb.lower(cb.coalesce(root.get("description"), "")), kw)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<PermissionEntity> pageResult = permissionRepository.findAll(spec, pageable);
        List<PermissionResponse> data = pageResult.getContent().stream().map(PermissionResponse::new).toList();
        return new SearchResponse<>(data, pageResult.getTotalElements());
    }
}

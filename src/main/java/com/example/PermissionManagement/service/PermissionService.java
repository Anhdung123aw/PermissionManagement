package com.example.PermissionManagement.service;

import com.example.PermissionManagement.Utils.PageableUtils;
import com.example.PermissionManagement.config.HasPermissionCms;
import com.example.PermissionManagement.dto.request.PermissionFilterRequest;
import com.example.PermissionManagement.dto.request.PermissionRequest;
import com.example.PermissionManagement.dto.request.PermissionUpdateRequest;
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

    @HasPermissionCms(value={"PERMISSION_MAKER"})
    public PermissionResponse createPermission(PermissionRequest request){
        PermissionEntity entity = new PermissionEntity();
        entity.setPermissionCode(request.getPermissionCode());
        entity.setPermissionName(request.getPermissionName());
        entity.setDescription(request.getDescription());
        entity = permissionRepository.save(entity);
        return new PermissionResponse(entity);
    }
    @HasPermissionCms(value={"PERMISSION_VIEWER", "PERMISSION_CHECKER", "PERMISSION_MAKER"})
    public List<PermissionResponse> getPermissions() {
        return permissionRepository.findAll().stream()
                .map(PermissionResponse::new)
                .toList();
    }
    @HasPermissionCms(value={"PERMISSION_VIEWER", "PERMISSION_CHECKER", "PERMISSION_MAKER"})
    public PermissionResponse getPermission(String permissionCode) {
        PermissionEntity entity = permissionRepository.findById(permissionCode)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        return new PermissionResponse(entity);
    }

    // Cập nhật tên hiển thị và mô tả của Quyền hạn (Mã quyền là bất biến)
    @HasPermissionCms(value={"PERMISSION_MAKER"})
    public PermissionResponse updatePermission(String permissionCode, PermissionUpdateRequest request) {
        PermissionEntity entity = permissionRepository.findById(permissionCode)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));

        if (StringUtils.hasText(request.getPermissionName())) {
            entity.setPermissionName(request.getPermissionName().trim());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription().trim());
        }

        entity = permissionRepository.save(entity);
        return new PermissionResponse(entity);
    }
    @HasPermissionCms(value={"PERMISSION_MAKER"})
    public void deletePermission(String permissionCode) {
        if (!permissionRepository.existsById(permissionCode)) {
            throw new AppException(ErrorCode.PERMISSION_NOT_EXISTED);
        }
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

    @HasPermissionCms(value={"PERMISSION_VIEWER", "PERMISSION_CHECKER", "PERMISSION_MAKER"})
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

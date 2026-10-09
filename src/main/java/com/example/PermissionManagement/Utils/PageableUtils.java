package com.example.PermissionManagement.Utils;

import com.example.PermissionManagement.dto.request.SearchRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import org.springframework.data.domain.Pageable;

public class PageableUtils {
    public static Pageable getPageable(SearchRequest<?> req, String defaultSortField) {
        SearchRequest.Pagination pagination = req.getPagination();
        int page = (pagination != null && pagination.getPage() != null && pagination.getPage() > 0)
                ? pagination.getPage() - 1 : 0;
        int size = (pagination != null && pagination.getSize() != null && pagination.getSize() > 0)
                ? pagination.getSize() : 10;
        SearchRequest.Sort sort = req.getSort();
        if (sort != null && StringUtils.hasText(sort.getField())) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(sort.getOrder())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, sort.getField()));
        }
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, defaultSortField));
    }
}

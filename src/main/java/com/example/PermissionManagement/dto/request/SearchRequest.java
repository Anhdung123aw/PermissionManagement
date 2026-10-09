package com.example.PermissionManagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest<T> {
    private Pagination pagination = new Pagination();
    private Sort sort;
    private T filter;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination{
        private Integer page = 1 ;
        private Integer size = 10 ;

    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sort{
        private String field;
        private String order;
    }
}

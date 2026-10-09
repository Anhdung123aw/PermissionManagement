package com.example.PermissionManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;



public record SearchResponse<T>(List<T> data, Long totalCount) {
}


package com.example.PermissionManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403,"UNAUTHORIZED",HttpStatus.UNAUTHORIZED),
    INVALID_PAGINATION(1007, "Phân trang không hợp lệ (page >= 1, 1 <= size <= 100)", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(1008, "Vai trò không tồn tại", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_EXISTED(1009, "Quyền hạn không tồn tại", HttpStatus.NOT_FOUND),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}

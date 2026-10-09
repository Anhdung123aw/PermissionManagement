package com.example.PermissionManagement.service;

import com.example.PermissionManagement.Utils.PageableUtils;
import com.example.PermissionManagement.dto.request.SearchRequest;
import com.example.PermissionManagement.dto.request.UserCreationRequest;
import com.example.PermissionManagement.dto.request.UserFilterRequest;
import com.example.PermissionManagement.dto.request.UserUpdateRequest;
import com.example.PermissionManagement.dto.response.SearchResponse;
import com.example.PermissionManagement.dto.response.UserResponse;
import com.example.PermissionManagement.entity.UserEntity;
import com.example.PermissionManagement.exception.AppException;
import com.example.PermissionManagement.exception.ErrorCode;
import com.example.PermissionManagement.repository.RoleRepository;
import com.example.PermissionManagement.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
@Service
@RequiredArgsConstructor

@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request){
        if(userRepository.existsByUserName(request.getUserName())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        UserEntity user = new UserEntity();
        user.setUserName(request.getUserName());
        user.setPassWord(passwordEncoder.encode(request.getPassWord()));
        user.setEmail(request.getUserName()+"@gmail.com");

        user = userRepository.save(user);

        return new UserResponse(user);
    }
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (request.getPassWord() != null && !request.getPassWord().isBlank()) {
            user.setPassWord(passwordEncoder.encode(request.getPassWord()));
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getRoles() != null) {
            var roles = roleRepository.findAllById(request.getRoles());
            user.setRoles(new HashSet<>(roles));
        }
        user = userRepository.save(user);
        return new UserResponse(user);
    }
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        userRepository.deleteById(userId);
    }

    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream().map(UserResponse::new).toList();

    }

    public UserResponse getUser(Long userId){
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));
        return new UserResponse(user);

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
    // 2. Pha Handle Search & Pagination
    @Transactional(readOnly = true)
    public SearchResponse<UserResponse> searchUsers(SearchRequest<UserFilterRequest> request) {
        // Validate dữ liệu đầu vào
        validateSearch(request);
        // Lấy Pageable qua Helper chung
        Pageable pageable = PageableUtils.getPageable(request, "id");
        // Xây dựng Specification lọc động theo keyword (username hoặc email)
        UserFilterRequest filter = request.getFilter();
        Specification<UserEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter != null && StringUtils.hasText(filter.getKeyword())) {
                String kw = "%" + filter.getKeyword().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("userName")), kw),
                        cb.like(cb.lower(root.get("email")), kw)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        // Query Spring Data JPA
        Page<UserEntity> pageResult = userRepository.findAll(spec, pageable);
        // Trả về SearchResponse chuẩn VAB
        List<UserResponse> data = pageResult.getContent().stream().map(UserResponse::new).toList();
        return new SearchResponse<>(data, pageResult.getTotalElements());
    }
}


package com.example.PermissionManagement.service;

import com.example.PermissionManagement.dto.request.UserCreationRequest;
import com.example.PermissionManagement.dto.request.UserUpdateRequest;
import com.example.PermissionManagement.dto.response.UserResponse;
import com.example.PermissionManagement.entity.UserEntity;
import com.example.PermissionManagement.exception.AppException;
import com.example.PermissionManagement.exception.ErrorCode;
import com.example.PermissionManagement.repository.RoleRepository;
import com.example.PermissionManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

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


    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return new UserResponse(user);
    }

    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream().map(UserResponse::new).toList();

    }

    public UserResponse getUser(Long userId){
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));
        return new UserResponse(user);

    }
}


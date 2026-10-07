package com.example.PermissionManagement.entity;
import com.example.PermissionManagement.dto.response.UserResponse;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="USERS")

public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="USER_NAME")
    private String userName;

    @Column(name="PASS_WORD")
    private String passWord;

    @Column(name="EMAIL")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID",referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_CODE",referencedColumnName = "ROLE_CODE"))
    private Set<RoleEntity> roles = new HashSet<>();
}
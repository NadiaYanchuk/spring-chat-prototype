package com.example.chat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    
    @ToString.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 2, max = 50, message = "Username should be between 2 and 50 characters")
    @Column(nullable = false, unique = true)
    private String username;

    @ToString.Exclude
    @Size(min = 6, message = "Password length must be more than 5 characters")
    @NotBlank(message = "Password is mandatory")
    @Column(nullable = false)
    private String password;

    @ToString.Exclude
    @Column(nullable = false, unique = true)
    @Email(message = "Please enter a correct email")
    private String email;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Column(name = "join_time", nullable = false)
    private LocalDateTime joinTime;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @Column(name = "is_online")
    private Boolean isOnline = false;

    public enum UserStatus {
        ONLINE,
        OFFLINE,
        AWAY
    }

    public UserEntity(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.joinTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.status = UserStatus.OFFLINE;
        this.isOnline = false;
    }
    
    @PrePersist
    protected void onCreate() {
        if (joinTime == null) {
            joinTime = LocalDateTime.now();
        }
        if (lastActivity == null) {
            lastActivity = LocalDateTime.now();
        }
        if (status == null) {
            status = UserStatus.OFFLINE;
        }
        if (isOnline == null) {
            isOnline = false;
        }
    }
}
package jye.budget.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long userId;
    private String email;
    private String password;
    private boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

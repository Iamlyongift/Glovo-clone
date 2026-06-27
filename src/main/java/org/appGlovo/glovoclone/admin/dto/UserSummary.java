package org.appGlovo.glovoclone.admin.dto;

import lombok.Builder;
import lombok.Data;
import org.appGlovo.glovoclone.user.Role;

import java.time.LocalDateTime;

@Data
@Builder
public class UserSummary {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
}
package org.example.pcroom.feature.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.pcroom.feature.user.enums.UserRole;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UserListDto {

    private Long userId;
    private String nickname;
    private String email;
    private UserRole role;
    private LocalDateTime createDate;

}

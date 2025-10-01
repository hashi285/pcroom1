package org.example.pcroom.feature.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @Schema(description = "사용자 이메일", example = "test@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "사용자 비밀번호", example = "password123")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "사용자 이름", example = "문수혁")
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
}

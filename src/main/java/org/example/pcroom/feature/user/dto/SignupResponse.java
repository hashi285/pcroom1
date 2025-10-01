package org.example.pcroom.feature.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponse {

    @Schema(description = "사용자 이메일", example = "test@example.com")
    private String email;

    @Schema(description = "회원가입 성공 여부", example = "회원가입 성공 / 이미 존재하는 이메일입니다.")
    private String message;
}

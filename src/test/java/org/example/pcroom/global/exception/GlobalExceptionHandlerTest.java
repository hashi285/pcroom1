package org.example.pcroom.global.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("IllegalArgumentException 발생 시 400 에러를 반환해야 한다")
    void testIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 인자입니다."));
    }

    @Test
    @DisplayName("EntityNotFoundException 발생 시 404 에러를 반환해야 한다")
    void testEntityNotFoundException() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("데이터를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("AccessDeniedException 발생 시 403 에러를 반환해야 한다")
    void testAccessDeniedException() throws Exception {
        mockMvc.perform(get("/test/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("해당 권한이 없습니다: 접근 거부"));
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 발생 시 400 에러를 반환해야 한다 (Validation 실패)")
    void testValidationException() throws Exception {
        String invalidJson = "{\"count\": -5}"; // count는 1 이상이어야 함

        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("숫자는 1 이상이어야 합니다."));
    }

    @Test
    @DisplayName("MaxUploadSizeExceededException 발생 시 413 에러를 반환해야 한다")
    void testMaxUploadSizeExceededException() throws Exception {
        mockMvc.perform(get("/test/upload-limit"))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.status").value(413))
                .andExpect(jsonPath("$.message").value("업로드 가능한 파일 용량을 초과했습니다."));
    }

    @Test
    @DisplayName("기타 Exception 발생 시 500 에러를 반환해야 한다")
    void testException() throws Exception {
        mockMvc.perform(get("/test/exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요."));
    }

    // 예외를 발생시키기 위한 더미 컨트롤러
    @RestController
    static class DummyController {
        @GetMapping("/test/illegal-argument")
        public void throwIllegalArgument() {
            throw new IllegalArgumentException("잘못된 인자입니다.");
        }

        @GetMapping("/test/not-found")
        public void throwNotFound() {
            throw new EntityNotFoundException("데이터를 찾을 수 없습니다.");
        }

        @GetMapping("/test/access-denied")
        public void throwAccessDenied() {
            throw new AccessDeniedException("접근 거부");
        }

        @GetMapping("/test/upload-limit")
        public void throwUploadLimit() {
            throw new MaxUploadSizeExceededException(2000L);
        }

        @GetMapping("/test/exception")
        public void throwException() {
            throw new RuntimeException("알 수 없는 서버 에러");
        }

        @PostMapping("/test/validation")
        public void throwValidation(@Valid @RequestBody DummyDto dto) {
            // 로직 없음
        }
    }

    static class DummyDto {
        @Min(value = 1, message = "숫자는 1 이상이어야 합니다.")
        private int count;

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }
}

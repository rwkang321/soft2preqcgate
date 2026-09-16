package com.soft2preqcgate;

import com.soft2preqcgate.common.security.SecurityConfig;
import com.soft2preqcgate.health.controller.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * DB/Redis 없이 웹 계층만 띄워서 health 엔드포인트가 인증 없이 열려 있는지 확인한다.
 */
@WebMvcTest(
        controllers = HealthController.class,
        properties = {
                "soft2preqcgate.cors.allowed-origins[0]=http://localhost:9091",
                "soft2preqcgate.security.swagger-enabled=false",
                "soft2preqcgate.auth.login.bcrypt-strength=8"
        })
@Import(SecurityConfig.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthIsPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

}

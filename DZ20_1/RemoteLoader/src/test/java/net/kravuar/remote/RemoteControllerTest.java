package net.kravuar.remote;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RemoteControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RemoteProps remoteProps;

    @Test
    void givenValidUrl_WhenGetRemote_ThenReturnsOk() throws Exception {
        // Given
        String validUrl = URLEncoder.encode("https://example.com", StandardCharsets.UTF_8);

        // When & Then
        mockMvc.perform(get("/remote/get/{url}", validUrl))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidUrl_WhenGetRemote_ThenReturnsBadRequest() throws Exception {
        // Given
        String invalidUrl = URLEncoder.encode("invalid-url", StandardCharsets.UTF_8);

        // When
        MockHttpServletResponse response = mockMvc
                .perform(get("/remote/get/{url}", invalidUrl))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenSuccessfulResponse_WhenGetRemote_ThenSetsCacheHeaders() throws Exception {
        // Given
        String validUrl = URLEncoder.encode("https://example.com", StandardCharsets.UTF_8);

        // When & Then
        mockMvc.perform(get("/remote/get/{url}", validUrl))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(header().exists(remoteProps.cacheHeaderName()));
    }

    @Test
    void givenValidButInaccessible_WhenGetRemote_ThenReturnsInternalError() throws Exception {
        // Given
        String invalidUrl = URLEncoder.encode("https://unreachable.example.com", StandardCharsets.UTF_8);

        // When & Then
        mockMvc.perform(get("/remote/get/{url}", invalidUrl))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError());
    }
}

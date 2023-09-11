package com.example.MyBookShopApp.security.controller;


import com.example.MyBookShopApp.security.model.RegistrationForm;
import com.example.MyBookShopApp.security.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    public void testAuthController() throws Exception {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setPhone("+71111111111");
        registrationForm.setPassword("123456789");
        registrationForm.setEmail("test@email.ru");
        registrationForm.setName("test");

        mockMvc.perform(post("/reg")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationForm)))
            .andExpect(status().isOk())
            .andExpect(view().name("/signin"));
    }
}
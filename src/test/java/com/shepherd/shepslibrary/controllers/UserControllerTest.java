package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void shouldSuccessfullyCreateUserWithValidData() throws Exception {
        mockMvc.perform(post("/api/v1/user/signup"))
                .andExpect(status().isCreated())
                .andDo(print());
//                .andExpect()

    }


//    @Test
//public void createUser_WithValidData_ShouldCreateUser() throws Exception {
//    User newUser = new User(null, "newUser", "newuser@example.com", "password");
//
//    mockMvc.perform(post("/api/users")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(asJsonString(newUser)))
//            .andExpect(status().isCreated())
//            .andExpect(header().exists("Location"))
//            .andExpect(jsonPath("$.username", is("newUser")));
//}
}

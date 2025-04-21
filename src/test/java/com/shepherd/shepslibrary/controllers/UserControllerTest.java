//package com.shepherd.shepslibrary.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
//import com.shepherd.shepslibrary.service.user.UserService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(UserController.class)
//class UserControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @TestConfiguration
//    static class TestConfig {
//        @Bean
//        public UserService userService() {
//            return Mockito.mock(UserService.class);
//        }
//    }
//
//
//    @Test
//    void shouldSuccessfullyCreateUserWithValidData() throws Exception {
//        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
//        registerUserRequest.setFirstName("Ladipo");
//        registerUserRequest.setLastName("Ariyo");
//        registerUserRequest.setEmail("email@grr.la");
//        registerUserRequest.setPassword("Password123$");
//        registerUserRequest.setGender("MALE");
//
////        RegisterUserResponse registerUserResponse  = RegisterUserResponse.builder().build();
//
//        mockMvc.perform(
//                post("/api/v1/user/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(registerUserRequest))
//                )
//                .andExpect(status().isCreated())
//                .andDo(print());
////                .andExpect()
//
//    }
//
//
////        UserResponse mockResponse = new UserResponse(); // Replace with actual class used in your controller's return type
////        mockResponse.setId(1L); // assuming your response includes an ID
////        mockResponse.setEmail("email@grr.la");
////
////        when(userService.register(registerUserRequest)).thenReturn(mockResponse);
////
////        // Act & Assert
////        mockMvc.perform(post("/api/v1/user/signup")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(registerUserRequest)))
////                .andExpect(status().isCreated())
////                .andDo(print());
////    }
////}
//
//
//
//
//
////    @Test
////public void createUser_WithValidData_ShouldCreateUser() throws Exception {
////    User newUser = new User(null, "newUser", "newuser@example.com", "password");
////
////    mockMvc.perform(post("/api/users")
////            .contentType(MediaType.APPLICATION_JSON)
////            .content(asJsonString(newUser)))
////            .andExpect(status().isCreated())
////            .andExpect(header().exists("Location"))
////            .andExpect(jsonPath("$.username", is("newUser")));
////}
//}

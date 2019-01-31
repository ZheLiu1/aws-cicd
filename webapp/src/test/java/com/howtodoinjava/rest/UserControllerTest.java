package com.howtodoinjava.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.rest.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    ObjectMapper mapper;


    @Before
    public void setupMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Transactional
    public void test() throws Exception {
        User user = new User();
        user.setUser_id(10000);
        user.setUser_password("ac3af72d9f95161a502fd326865c2f15");
        user.setUser_name("123@123.com");

        String userJson = mapper.writeValueAsString(user);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(userJson.getBytes()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}

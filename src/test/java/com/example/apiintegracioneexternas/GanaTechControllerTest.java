package com.example.apiintegracioneexternas;

import com.example.apiintegracioneexternas.controller.GanaTechController;
import com.example.apiintegracioneexternas.service.GanaTechService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(GanaTechController.class)
public class GanaTechControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GanaTechService ganaTechService ;

    @Test
    void testConsultaSegip(){

    }
}

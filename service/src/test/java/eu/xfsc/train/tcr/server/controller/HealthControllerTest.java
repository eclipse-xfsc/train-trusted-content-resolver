package eu.xfsc.train.tcr.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import uniresolver.UniResolver;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;
	@MockBean
	private UniResolver uniResolver;

    @Test
    public void getGoodHealthShouldReturnSuccessResponse() throws Exception {
      
        when(uniResolver.methods()).thenReturn(Set.of("key", "web"));
    	
        mockMvc
            .perform(MockMvcRequestBuilders.get("/actuator/health")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.parseMediaType("application/vnd.spring-boot.actuator.v3+json")))
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.components.did-resolver.status").value("UP"))
            .andExpect(jsonPath("$.components.dns-resolver.status").value("UP"))
            .andExpect(jsonPath("$.components.dns-resolver.details.dnsSecEnabled").value(true));
    }

    @Test
    public void getWrongDidHealthShouldReturnErrorResponse() throws Exception {
      
        when(uniResolver.methods()).thenReturn(Set.of());
    	
        mockMvc
            .perform(MockMvcRequestBuilders.get("/actuator/health")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError())
            .andExpect(content().contentType(MediaType.parseMediaType("application/vnd.spring-boot.actuator.v3+json")))
            .andExpect(jsonPath("$.status").value("OUT_OF_SERVICE"))
            .andExpect(jsonPath("$.components.did-resolver.status").value("OUT_OF_SERVICE"))
            .andExpect(jsonPath("$.components.dns-resolver.status").value("UP"));
    }

}

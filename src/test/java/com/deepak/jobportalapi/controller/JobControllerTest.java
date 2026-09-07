package com.deepak.jobportalapi.controller;

import com.deepak.jobportalapi.service.CompanyService;
import com.deepak.jobportalapi.service.JobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @MockBean
    private CompanyService companyService;

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void shouldReturnJobsWhenAuthenticatedAsCandidate() throws Exception {
        when(jobService.getAllJobs()).thenReturn(List.of());

        mockMvc.perform(get("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void shouldReturnForbiddenWhenCandidateTriesToCreateJob() throws Exception {
        String jobJson = """
            {
                "title": "Software Engineer",
                "description": "Develop backend systems",
                "location": "Remote",
                "salary": "10 LPA",
                "companyId": 1
            }
            """;

        mockMvc.perform(post("/api/jobs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "RECRUITER")
    void shouldAllowRecruiterToCreateJob() throws Exception {
        String jobJson = """
            {
                "title": "Software Engineer",
                "description": "Develop backend systems",
                "location": "Remote",
                "salary": "10 LPA",
                "companyId": 1
            }
            """;

        mockMvc.perform(post("/api/jobs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobJson))
                .andExpect(status().isCreated());
    }
}
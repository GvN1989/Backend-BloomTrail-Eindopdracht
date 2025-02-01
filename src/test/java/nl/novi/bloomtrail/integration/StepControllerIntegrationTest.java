package nl.novi.bloomtrail.integration;

import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.repositories.StepRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc (addFilters = false)
@ActiveProfiles("test")
public class StepControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StepRepository stepRepository;

    @Test
    void shouldCreateStepSuccesfully() throws Exception {

        String stepJson = """
                {
                    "coachingProgramId" : 1,
                    "stepName" : "Test Step"
                }
                """;

        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/step")
                        .contentType(APPLICATION_JSON)
                        .content(stepJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void shouldReturnStepById() throws Exception {

        Step step = new Step();
        step.setStepName("Step Test");
        step = stepRepository.save(step);

        mockMvc.perform(MockMvcRequestBuilders.get("/step/" + step.getStepId())
                        .contentType(APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stepName", is("Step Test")));
    }
}

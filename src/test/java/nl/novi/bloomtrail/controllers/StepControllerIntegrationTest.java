package nl.novi.bloomtrail.controllers;

import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.User;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc (addFilters = false)
@ActiveProfiles("test")
public class StepControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StepRepository stepRepository;

    @Autowired
    private CoachingProgramRepository coachingProgramRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateStepSuccesfully() throws Exception {
        User client = new User();
        client.setUsername("testUser");
        client.setEmail("testuser@example.com");
        client.setPassword("password123");
        client = userRepository.save(client);

        User coach = new User();
        coach.setUsername("testCoach");
        coach.setEmail("testCoach@example.com");
        coach.setPassword("password456");
        coach = userRepository.save(coach);

        CoachingProgram coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramName("Test Coaching Program");
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);
        coachingProgram.setStartDate(LocalDate.of(2025, 4, 1));
        coachingProgram.setEndDate(LocalDate.of(2025, 12, 31));
        coachingProgram = coachingProgramRepository.save(coachingProgram);

        String requestJson = """
        {
            "coachingProgramId": %d,
            "stepName": "Test Step",
            "sequence": 1,
            "stepStartDate": "02-06-2025",
            "stepEndDate": "01-08-2025",
            "stepGoal": "Test goal"
        }
    """.formatted(coachingProgram.getCoachingProgramId());

        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/step")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stepName").value("Test Step"));
    }

    @Test
    void shouldReturnStepById() throws Exception {

        Step step = new Step();
        step.setStepName("Step Test");
        step.setSequence(1);
        step = stepRepository.save(step);

        mockMvc.perform(MockMvcRequestBuilders.get("/step/" + step.getStepId())
                        .contentType(APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stepName", is("Step Test")));
    }
}

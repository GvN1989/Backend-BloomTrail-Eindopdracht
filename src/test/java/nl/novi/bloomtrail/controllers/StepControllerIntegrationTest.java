package nl.novi.bloomtrail.controllers;

import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.User;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


import java.time.LocalDate;
import java.util.List;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
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
    @Mock
    private CoachingProgram coachingProgram;

    @BeforeEach
    void setUp() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "henk", "zegikniet", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        User client = new User();
        client.setUsername("testUser");
        client.setEmail("testuser@example.com");
        client.setPassword("password123");
        client.setApikey("ValidClientApiKey1234567890");
        client = userRepository.save(client);

        User coach = new User();
        coach.setUsername("testCoach");
        coach.setEmail("testCoach@example.com");
        coach.setPassword("password456");
        coach.setApikey("ValidCoachApiKey9876543210");
        coach = userRepository.save(coach);


        coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramName("Test Coaching Program");
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);
        coachingProgram.setStartDate(LocalDate.parse("2025-01-01"));
        coachingProgram.setEndDate(LocalDate.parse("2025-08-01"));
        coachingProgram = coachingProgramRepository.save(coachingProgram);
    }

    @Test
    void shouldCreateStepSuccesfully() throws Exception {


        String requestJson = """
            {
                "stepName": "Test Step",
                "sequence": 1,
                "stepStartDate": "05-06-2025",
                "stepEndDate": "01-08-2025",
                "stepGoal": "Test goal"
            }
            """;

        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/step/coaching-programs/"+ coachingProgram.getCoachingProgramId() +"/steps")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].stepName").value("Test Step"));
    }

    @Test
    void shouldReturnStepById() throws Exception {

        Step step = new Step();
        step.setStepName("Step Test");
        step.setSequence(1);
        step.setStepStartDate(LocalDate.parse("2025-01-01"));
        step.setStepEndDate(LocalDate.parse("2025-02-01"));
        step.setCoachingProgram(coachingProgram);
        step = stepRepository.save(step);

        mockMvc.perform(MockMvcRequestBuilders.get("/step/" + step.getStepId())
                        .contentType(APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stepName", is("Step Test")));
    }
}

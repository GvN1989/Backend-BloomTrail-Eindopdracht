package nl.novi.bloomtrail.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.User;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
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



import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc (addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class StepControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramName("Test Coaching Program");
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);
        coachingProgram.setStartDate(LocalDate.parse("01-01-2025", formatter));
        coachingProgram.setEndDate(LocalDate.parse("01-08-2025", formatter));
        coachingProgram = coachingProgramRepository.save(coachingProgram);
    }

    @Test
    void shouldCreateStepSuccesfully() throws Exception {

        StepInputDto inputDto = new StepInputDto();
        inputDto.setStepName("Test Step");
        inputDto.setStepGoal("Test Goal");
        inputDto.setStepStartDate(LocalDate.of(2025, 6, 6));
        inputDto.setStepEndDate(LocalDate.of(2025, 8, 1));
        inputDto.setCompleted(false);

        String json = objectMapper.writeValueAsString(inputDto);

        System.out.println("Serialized JSON:\n" + json);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/step/coaching-programs/" + coachingProgram.getCoachingProgramId() + "/steps")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].stepName").value("Test Step"));
    }

    @Test
    void shouldReturnStepById() throws Exception {

        Step step = new Step();
        step.setStepName("Step Test");
        step.setStepGoal("learn the basis of this test");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        step.setStepStartDate(LocalDate.parse("05-06-2025", formatter));
        step.setStepEndDate(LocalDate.parse("01-08-2025",formatter));

        step.setCoachingProgram(coachingProgram);
        step = stepRepository.save(step);

        mockMvc.perform(MockMvcRequestBuilders.get("/step/" + step.getStepId())
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stepName", is("Step Test")));
    }
}

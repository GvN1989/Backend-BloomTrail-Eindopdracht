package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.User;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.repositories.UserRepository;
import nl.novi.bloomtrail.utils.SecurityTestHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.springframework.security.core.context.SecurityContextHolder;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc (addFilters = false)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Transactional
public class StepServiceIntegrationTest {

    @Autowired
    private StepService stepService;

    @Autowired
    private StepRepository stepRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoachingProgramRepository coachingProgramRepository;
    private CoachingProgram coachingProgram;
    private Step step;

    @BeforeEach
    void setup() {
        SecurityTestHelper.authenticateAs("henk", "ADMIN");
    }
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAddStepsToProgram_Success() {

        User client = new User();
        client.setUsername("testUser");
        client.setEmail("testuser@example.com");
        client.setPassword("password123");
        client.setApikey("FakeApiKey1234567890");
        client = userRepository.save(client);

        User coach = new User();
        coach.setUsername("testCoach");
        coach.setEmail("testCoach@example.com");
        coach.setPassword("password456");
        coach.setApikey("CoachKey98765432109876");
        coach = userRepository.save(coach);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramName("Test Coaching Program");
        coachingProgram.setStartDate(LocalDate.parse("01-01-2025", formatter));
        coachingProgram.setEndDate(LocalDate.parse("01-08-2025", formatter));
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);
        coachingProgram = coachingProgramRepository.save(coachingProgram);

        StepInputDto stepInputDto = new StepInputDto();
        stepInputDto.setCoachingProgramId(coachingProgram.getCoachingProgramId());
        stepInputDto.setStepName("Integration Test Step");
        stepInputDto.setStepGoal("confirm if the integration works");
        stepInputDto.setCompleted(false);
        stepInputDto.setStepStartDate(LocalDate.parse("08-01-2025", formatter));
        stepInputDto.setStepEndDate(LocalDate.parse("01-02-2025", formatter));

        List<Step> savedSteps = stepService.addStepsToProgram(Collections.singletonList(stepInputDto));

        CoachingProgram updatedProgram = coachingProgramRepository.findById(coachingProgram.getCoachingProgramId())
                .orElseThrow(() -> new RuntimeException("Coaching program not found"));

        Assertions.assertEquals(1, savedSteps.size(), "Exactly one step should be saved");
        Step savedStep = savedSteps.get(0);
        Assertions.assertEquals("Integration Test Step", savedStep.getStepName(), "Step name should match");
        Assertions.assertTrue(updatedProgram.getTimeline().contains(savedStep), "Step should be added to Coaching Program timeline");
    }

    @Test
    void testFindById_Success() {

        User client = new User();
        client.setUsername("henk");
        client.setEmail("testuser@example.com");
        client.setPassword("password123");
        client.setApikey("FakeApiKey1234567890");
        client = userRepository.save(client);

        User coach = new User();
        coach.setUsername("testCoach");
        coach.setEmail("testCoach@example.com");
        coach.setPassword("password456");
        coach.setApikey("CoachKey98765432109876");
        coach = userRepository.save(coach);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramName("Test Coaching Program");
        coachingProgram.setStartDate(LocalDate.parse("01-01-2025", formatter));
        coachingProgram.setEndDate(LocalDate.parse("01-08-2025", formatter));
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);
        coachingProgram = coachingProgramRepository.save(coachingProgram);

        step = new Step();
        step.setStepName("Existing Step");
        step.setStepGoal("Deep dive in goal");
        step.setStepStartDate(LocalDate.parse("08-01-2025", formatter));
        step.setStepEndDate(LocalDate.parse("01-02-2025", formatter));
        step.setCoachingProgram(coachingProgram);
        step = stepRepository.save(step);

        Step foundStep = stepService.findById(step.getStepId());

        Assertions.assertNotNull(foundStep, "The step should not be null");
        Assertions.assertEquals("Existing Step", foundStep.getStepName(), "Step name should match");
        Assertions.assertEquals(coachingProgram.getCoachingProgramId(), foundStep.getCoachingProgram().getCoachingProgramId(), "Step should be linked to correct Coaching Program");
    }

    @Test
    void testDeleteStep_Success() {
        User client = new User();
        client.setUsername("henk");
        client.setEmail("testuser@example.com");
        client.setPassword("password123");
        client.setApikey("FakeApiKey1234567890");
        client = userRepository.save(client);

        User coach = new User();
        coach.setUsername("testCoach");
        coach.setEmail("testCoach@example.com");
        coach.setPassword("password456");
        coach.setApikey("CoachKey98765432109876");
        coach = userRepository.save(coach);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        CoachingProgram coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramName("Test Program");
        coachingProgram.setGoal("Test Goal");
        coachingProgram.setStartDate(LocalDate.parse("01-01-2025", formatter));
        coachingProgram.setEndDate(LocalDate.parse("01-08-2025", formatter));
        coachingProgram.setCoach(coach);
        coachingProgram.setClient(client);
        coachingProgram = coachingProgramRepository.save(coachingProgram);

        step = new Step();
        step.setStepName("Delete Me");
        step.setStepGoal("Deep dive in goal");
        step.setStepStartDate(LocalDate.parse("08-01-2025", formatter));
        step.setStepEndDate(LocalDate.parse("01-02-2025", formatter));
        step.setCoachingProgram(coachingProgram);
        step = stepRepository.save(step);

        stepService.deleteStep(step.getStepId());

        Optional<Step> deletedStep = stepRepository.findById(step.getStepId());
        Assertions.assertTrue(deletedStep.isEmpty(), "The step should be deleted");
    }

}


package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.User;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import nl.novi.bloomtrail.helper.DateConverter;

import java.time.LocalDate;
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

        coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramName("Test Coaching Program");
        coachingProgram.setStartDate(LocalDate.parse("2025-01-01"));
        coachingProgram.setEndDate(LocalDate.parse("2025-08-01"));
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);
        coachingProgram = coachingProgramRepository.save(coachingProgram);

        StepInputDto stepInputDto = new StepInputDto();
        stepInputDto.setCoachingProgramId(coachingProgram.getCoachingProgramId());
        stepInputDto.setStepName("Integration Test Step");
        stepInputDto.setStepStartDate(DateConverter.convertToDate(LocalDate.parse("2025-01-08")));
        stepInputDto.setStepEndDate(DateConverter.convertToDate(LocalDate.parse("2025-02-01")));

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

        coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramName("Test Coaching Program");
        coachingProgram.setStartDate(LocalDate.parse("2025-01-01"));
        coachingProgram.setEndDate(LocalDate.parse("2025-08-01"));
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);
        coachingProgram = coachingProgramRepository.save(coachingProgram);

        step = new Step();
        step.setStepName("Existing Step");
        step.setSequence(1);
        step.setStepStartDate(LocalDate.parse("2025-01-08"));
        step.setStepEndDate(LocalDate.parse("2025-02-01"));
        step.setCoachingProgram(coachingProgram);
        step = stepRepository.save(step);

        Step foundStep = stepService.findById(step.getStepId());

        Assertions.assertNotNull(foundStep, "The step should not be null");
        Assertions.assertEquals("Existing Step", foundStep.getStepName(), "Step name should match");
        Assertions.assertEquals(coachingProgram.getCoachingProgramId(), foundStep.getCoachingProgram().getCoachingProgramId(), "Step should be linked to correct Coaching Program");
    }

    @Test
    void testDeleteStep_Success() {
        step = new Step();
        step.setStepName("Delete Me");
        step.setSequence(1);
        step.setStepStartDate(LocalDate.parse("2025-01-08"));
        step.setStepEndDate(LocalDate.parse("2025-02-01"));
        step.setCoachingProgram(coachingProgram);
        step = stepRepository.save(step);

        stepService.deleteStep(step.getStepId());

        Optional<Step> deletedStep = stepRepository.findById(step.getStepId());
        Assertions.assertTrue(deletedStep.isEmpty(), "The step should be deleted");
    }

}


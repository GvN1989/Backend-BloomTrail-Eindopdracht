package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.BloomTrailApplication;
import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.User;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;


@SpringBootTest(classes = BloomTrailApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class StepServiceIntegrationTest {

    @Autowired
    private StepService stepService;

    @Autowired
    private StepRepository stepRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoachingProgramRepository coachingProgramRepository;

    @Test
    void testAddStepToProgram_Success() {

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
        coachingProgram.setStartDate(LocalDate.of(2025, 4, 1));
        coachingProgram.setEndDate(LocalDate.of(2025, 12, 31));
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);
        coachingProgram = coachingProgramRepository.save(coachingProgram);

        coachingProgram = coachingProgramRepository.findById(coachingProgram.getCoachingProgramId())
                .orElseThrow(() -> new RuntimeException("Coaching program not found"));

        StepInputDto stepInputDto = new StepInputDto();
        stepInputDto.setCoachingProgramId(coachingProgram.getCoachingProgramId());
        stepInputDto.setStepName("Integration Test Step");
        stepInputDto.setSequence(1);
        stepInputDto.setStepStartDate(LocalDate.of(2025, 6, 2));
        stepInputDto.setStepEndDate(LocalDate.of(2025, 8, 1));
        Step savedStep = stepService.addStepToProgram(stepInputDto);

        CoachingProgram updatedProgram = coachingProgramRepository.findByIdWithSteps(coachingProgram.getCoachingProgramId())
                .orElseThrow(() -> new RuntimeException("Coaching program not found"));

        Assertions.assertNotNull(savedStep);
        Assertions.assertEquals("Integration Test Step", savedStep.getStepName());
    }

    @Test
    void testFindById_Success() {
        Step step = new Step();
        step.setStepName("Existing Step");
        step.setSequence(1);
        step.setStepStartDate(LocalDate.of(2025, 1, 10));
        step.setStepEndDate(LocalDate.of(2025, 1, 15));

        step = stepRepository.save(step);

        Step foundStep = stepService.findById(step.getStepId());

        Assertions.assertNotNull(foundStep);
        Assertions.assertEquals("Existing Step", foundStep.getStepName());
    }

    @Test
    void testDeleteStep_Success() throws Exception {
        Step step = new Step();
        step.setStepName("Delete Me");
        step.setSequence(1);
        step.setStepStartDate(LocalDate.of(2025, 1, 10));
        step.setStepEndDate(LocalDate.of(2025, 1, 15));

        step = stepRepository.save(step);

        stepService.deleteStep(step.getStepId());

        Assertions.assertFalse(stepRepository.findById(step.getStepId()).isPresent());
    }
}



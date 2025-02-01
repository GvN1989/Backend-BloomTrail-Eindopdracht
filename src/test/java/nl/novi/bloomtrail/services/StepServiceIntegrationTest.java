package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.BloomTrailApplication;
import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.repositories.StepRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest(classes = BloomTrailApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class StepServiceIntegrationTest {

    @Autowired
    private StepService stepService;

    @Autowired
    private StepRepository stepRepository;

    @Test
    void testAddStepToProgram_Success() {

        StepInputDto stepInputDto = new StepInputDto();
        stepInputDto.setCoachingProgramId(1L);
        stepInputDto.setStepName("Integration Test Step");

        Step createdStep = stepService.addStepToProgram(stepInputDto);

        Assertions.assertNotNull(createdStep);
        Assertions.assertEquals("Integration Test Step", createdStep.getStepName());
    }

    @Test
    void testFindById_Success() {
        Step step = new Step();
        step.setStepName("Existing Step");
        step = stepRepository.save(step);

        Step foundStep = stepService.findById(step.getStepId());

        Assertions.assertNotNull(foundStep);
        Assertions.assertEquals("Existing Step", foundStep.getStepName());
    }

    @Test
    void testDeleteStep_Success() throws Exception {
        Step step = new Step();
        step.setStepName("Delete Me");
        step = stepRepository.save(step);

        stepService.deleteStep(step.getStepId());

        Assertions.assertFalse(stepRepository.findById(step.getStepId()).isPresent());
    }
}



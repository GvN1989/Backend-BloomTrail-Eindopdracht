package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.DateConverter;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.StepRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StepServiceUnitTest {

    @Mock
    private StepRepository stepRepository;
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private CoachingProgramService coachingProgramService;
    @Mock
    private CoachingProgramRepository coachingProgramRepository;
    @Mock
    private DownloadService downloadService;
    @InjectMocks
    private StepService stepService;
    private Step mockStep;
    private StepInputDto mockStepInputDto;
    private Assignment mockAssignment;

    private CoachingProgram mockCoachingProgram;

    @BeforeEach
    void setUp() {

        mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(1L);
        mockCoachingProgram.setCoachingProgramName("Test Coaching Program");
        mockCoachingProgram.setStartDate(LocalDate.parse("2025-01-01"));
        mockCoachingProgram.setEndDate(LocalDate.parse("2025-06-01"));

        mockCoachingProgram.setTimeline(new ArrayList<>());

        Mockito.lenient().when(coachingProgramRepository.findById(1L)).thenReturn(Optional.of(mockCoachingProgram));

        mockStep = new Step();
        mockStep.setStepId(1L);

        mockStepInputDto = new StepInputDto();
        mockStepInputDto.setCoachingProgramId(1L);

        mockAssignment = new Assignment();
        mockAssignment .setAssignmentId(1L);

        Mockito.lenient().when(coachingProgramRepository.findById(1L)).thenReturn(Optional.of(mockCoachingProgram));
        Mockito.lenient().when(validationHelper.validateStep(1L)).thenReturn(mockStep);
        Mockito.lenient().when(validationHelper.validateStep(1L)).thenReturn(mockStep);
        Mockito.lenient().when(validationHelper.validateAssignment(1L)).thenReturn(mockAssignment);
    }
    @Tag("unit")
    @Test
    void testFindById_Success() {
        when(validationHelper.validateStep(1L)).thenReturn(mockStep);

        Step result = stepService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getStepId());

        Mockito.verify(validationHelper, Mockito.times(1)).validateStep(1L);
    }

    @Tag("unit")
    @Test
    void testAddStepsToProgram_Success() {

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.findStepsByCoachingProgram(1L)).thenReturn(List.of());
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(coachingProgramRepository.save(any(CoachingProgram.class))).thenReturn(mockCoachingProgram);

        StepInputDto stepDto = new StepInputDto();
        stepDto.setCoachingProgramId(1L);
        stepDto.setStepName("Valid Step");
        stepDto.setSequence(1);
        stepDto.setStepStartDate(DateConverter.convertToDate(LocalDate.parse("2025-06-15")));
        stepDto.setStepEndDate(DateConverter.convertToDate(LocalDate.parse("2025-07-15")));

        Assertions.assertDoesNotThrow(() -> stepService.addStepsToProgram(List.of(stepDto)));

        Mockito.verify(validationHelper, Mockito.times(1)).validateCoachingProgram(1L);
        Mockito.verify(stepRepository, Mockito.times(1)).save(any(Step.class));
        Mockito.verify(coachingProgramRepository, Mockito.times(1)).save(any(CoachingProgram.class));
        Mockito.verify(coachingProgramService, Mockito.times(1)).updateProgramEndDate(1L);
    }


    @Tag("unit")
    @Test
    void testAddStepsToProgram_WhenSequenceAlreadyExists() {
        Step existingStep = new Step();
        existingStep.setSequence(1);
        existingStep.setStepStartDate(LocalDate.parse("2025-05-01"));
        existingStep.setStepEndDate(LocalDate.parse("2025-09-01"));

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.findStepsByCoachingProgram(1L)).thenReturn(List.of(existingStep));

        StepInputDto newStepDto = new StepInputDto();
        newStepDto.setCoachingProgramId(1L);
        newStepDto.setStepName("Duplicate Sequence Step");
        newStepDto.setSequence(1);
        newStepDto.setStepStartDate(DateConverter.convertToDate(LocalDate.parse("2025-06-01")));
        newStepDto.setStepEndDate(DateConverter.convertToDate(LocalDate.parse("2025-10-01")));

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> stepService.addStepsToProgram(List.of(newStepDto)));

        assertEquals("A step with sequence 1 already exists in this program.", exception.getMessage());

        Mockito.verify(validationHelper, Mockito.times(1)).validateCoachingProgram(1L);
        Mockito.verify(stepRepository, Mockito.times(1)).findStepsByCoachingProgram(1L);
    }
    @Tag("unit")
    @Test
    void testAddStepsToProgram_WhenStartDateConflictsWithSequence() {

        Step existingStep = new Step();
        existingStep.setSequence(1);
        existingStep.setStepStartDate(LocalDate.of(2025, 7, 1));

        when(stepRepository.findStepsByCoachingProgram(1L)).thenReturn(List.of(existingStep));

        StepInputDto newStepDto = new StepInputDto();
        newStepDto.setCoachingProgramId(1L);
        newStepDto.setStepName("Conflicting Start Date Step");
        newStepDto.setSequence(2);
        newStepDto.setStepStartDate(DateConverter.convertToDate(LocalDate.of(2025, 6, 20)));
        newStepDto.setStepEndDate(DateConverter.convertToDate(LocalDate.of(2025, 8, 20)));
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> stepService.addStepsToProgram(List.of(newStepDto)));

        assertEquals("The step's start date conflicts with the provided sequence.", exception.getMessage());

        Mockito.verify(stepRepository, Mockito.times(1)).findStepsByCoachingProgram(1L);
    }

    @Tag("unit")
    @Test
    void testAddStepsToProgram_WithMultipleExistingSteps() {
        Step existingStep1 = new Step();
        existingStep1.setSequence(1);
        existingStep1.setStepStartDate(LocalDate.of(2025, 5, 1));

        Step existingStep2 = new Step();
        existingStep2.setSequence(2);
        existingStep2.setStepStartDate(LocalDate.of(2025, 6, 10));

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.findStepsByCoachingProgram(1L)).thenReturn(List.of(existingStep1, existingStep2));

        StepInputDto newStepDto = new StepInputDto();
        newStepDto.setCoachingProgramId(1L);
        newStepDto.setStepName("Step with No Conflict");
        newStepDto.setSequence(3);
        newStepDto.setStepStartDate(DateConverter.convertToDate(LocalDate.of(2025, 7, 1)));
        newStepDto.setStepEndDate(DateConverter.convertToDate(LocalDate.of(2025, 8, 1)));

        Assertions.assertDoesNotThrow(() -> stepService.addStepsToProgram(List.of(newStepDto)));

        Mockito.verify(stepRepository, Mockito.times(1)).findStepsByCoachingProgram(1L);
        Mockito.verify(stepRepository, Mockito.times(1)).save(any(Step.class));
        Mockito.verify(coachingProgramRepository, Mockito.times(1)).save(any(CoachingProgram.class));
    }


    @Test
    void shouldThrowExceptionWhenStepDatesAreNull() {

        StepInputDto inputDto = new StepInputDto();
        inputDto.setCoachingProgramId(1L);
        inputDto.setStepStartDate(null);
        inputDto.setStepEndDate(null);


        when(validationHelper.validateCoachingProgram(1L)).thenReturn(new CoachingProgram());

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            stepService.addStepsToProgram(List.of(inputDto));
        });


        assertEquals("Step start date and end date cannot be null.", exception.getMessage());

        verify(validationHelper, times(1)).validateCoachingProgram(1L);
        verifyNoInteractions(stepRepository, coachingProgramRepository, coachingProgramService);
    }


    @Tag("unit")
    @Test
    void testFindById_ThrowsException_WhenStepNotFound() {
        when(validationHelper.validateStep(99L)).thenThrow(new NotFoundException("Step not found"));

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
            stepService.findById(99L);
        });

        assertEquals("Step not found", exception.getMessage());
        verify(validationHelper, times(1)).validateStep(99L);
    }
    @Tag("unit")
    @Test
    void testUpdateStep_Success() {
        mockStepInputDto.setStepName("Updated Step");

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Step result = stepService.updateStep(1L, mockStepInputDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated Step", result.getStepName());
        Mockito.verify(stepRepository, Mockito.times(1)).save(any(Step.class));
        Mockito.verify(coachingProgramService, Mockito.times(1)).updateProgramEndDate(1L);
    }

    @Tag("unit")
    @Test
    void testUpdateStep_CoversAllFields() {

        mockStepInputDto.setStepName("Updated Step");
        mockStepInputDto.setStepStartDate(DateConverter.convertToDate(LocalDate.parse("2025-01-01")));
        mockStepInputDto.setStepEndDate(DateConverter.convertToDate(LocalDate.parse("2025-01-10")));
        mockStepInputDto.setCompleted(true);
        mockStepInputDto.setStepGoal("New Goal");
        mockStepInputDto.setSequence(2);

        List<Long> sessionIds = List.of(101L, 102L);
        List<Long> assignmentIds = List.of(201L, 202L);

        mockStepInputDto.setSessionIds(sessionIds);
        mockStepInputDto.setAssignmentIds(assignmentIds);

        Session session1 = new Session();
        session1.setSessionId(101L);
        Session session2 = new Session();
        session2.setSessionId(102L);

        Assignment assignment1 = new Assignment();
        assignment1.setAssignmentId(201L);
        Assignment assignment2 = new Assignment();
        assignment2.setAssignmentId(202L);

        mockStep.setSession(new ArrayList<>());
        mockStep.setAssignment(new ArrayList<>());
        mockStep.setCoachingProgram(mockCoachingProgram);

        when(validationHelper.validateStep(1L)).thenReturn(mockStep);
        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(validationHelper.validateSessions(sessionIds)).thenReturn(List.of(session1, session2));
        when(validationHelper.validateAssignments(assignmentIds)).thenReturn(List.of(assignment1, assignment2));

        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Step result = stepService.updateStep(1L, mockStepInputDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated Step", result.getStepName());
        Assertions.assertEquals(LocalDate.parse("2025-01-01"), result.getStepStartDate());
        Assertions.assertEquals(LocalDate.parse("2025-01-10"), result.getStepEndDate());
        Assertions.assertTrue(result.getCompleted());
        Assertions.assertEquals("New Goal", result.getStepGoal());
        Assertions.assertEquals(2, result.getSequence());
        Assertions.assertEquals(2, result.getSession().size());
        Assertions.assertEquals(2, result.getAssignment().size());

        Mockito.verify(validationHelper, Mockito.times(1)).validateStep(1L);
        Mockito.verify(validationHelper, Mockito.times(1)).validateSessions(sessionIds);
        Mockito.verify(validationHelper, Mockito.times(1)).validateAssignments(assignmentIds);
        Mockito.verify(stepRepository, Mockito.times(1)).save(any(Step.class));
        Mockito.verify(coachingProgramService, Mockito.times(1)).updateProgramEndDate(anyLong());
    }

    @Tag("unit")
    @Test
    void testDeleteStep_Success() {

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(1L);
        mockStep.setCoachingProgram(mockCoachingProgram);

        when(validationHelper.validateStep(1L)).thenReturn(mockStep);

        stepService.deleteStep(1L);

        Mockito.verify(validationHelper, Mockito.times(1)).validateStep(1L);
        Mockito.verify(coachingProgramService, Mockito.times(1)).updateProgramEndDate(1L);
        Mockito.verify(stepRepository, Mockito.times(1)).delete(mockStep);
    }
    @Tag("unit")
    @Test
    void testMarkStepCompletionStatus_Success() {
        when(stepRepository.findById(1L)).thenReturn(Optional. of(mockStep));
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Step result = stepService.markStepCompletionStatus(1L, true);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getCompleted());

        Mockito.verify(stepRepository, times(1)).findById(1L);
        Mockito.verify(stepRepository, times(1)).save(any(Step.class));
    }



    @Tag("unit")
    @Test
    void testDownloadFilesForStep_Success() throws IOException {
        when(validationHelper.validateStep(1L)).thenReturn(mockStep);

        byte[] mockData = "mock file data".getBytes();
        when(downloadService.downloadFilesForEntity(any(Step.class))).thenReturn(mockData);

        byte[] result = stepService.downloadFilesForStep(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockData.length, result.length);

        Mockito.verify(validationHelper, Mockito.times(1)).validateStep(1L);
        Mockito.verify(downloadService, Mockito.times(1)).downloadFilesForEntity(mockStep);
    }



}

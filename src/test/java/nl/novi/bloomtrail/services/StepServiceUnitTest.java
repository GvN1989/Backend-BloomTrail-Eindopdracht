package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.repositories.AssignmentRepository;

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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
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
    private EntityValidationHelper validationHelper;

    @Mock
    private CoachingProgramService coachingProgramService;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private CoachingProgramRepository coachingProgramRepository;

    @Mock
    private DownloadService downloadService;

    @InjectMocks
    private StepService stepService;
    private Step mockStep;
    private StepInputDto mockStepInputDto;
    private Assignment mockAssignment;

    @BeforeEach
    void setUp() {

        mockStep = new Step();
        mockStep.setStepId(1L);

        mockStepInputDto = new StepInputDto();
        mockStepInputDto.setCoachingProgramId(1L);

        mockAssignment = new Assignment();
        mockAssignment .setAssignmentId(1L);

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
    void testFindById_ThrowsException_WhenStepNotFound() {
        when(validationHelper.validateStep(99L)).thenThrow(new RecordNotFoundException("Step not found"));

        RecordNotFoundException exception = Assertions.assertThrows(RecordNotFoundException.class, () -> {
            stepService.findById(99L);
        });

        assertEquals("Step not found", exception.getMessage());
        verify(validationHelper, times(1)).validateStep(99L);
    }
    @Tag("unit")
    @Test
    void testAddStepToProgram_Success() {
        StepInputDto mockStepInputDto = new StepInputDto();
        mockStepInputDto.setCoachingProgramId(1L);
        mockStepInputDto.setStepName("Test Step");
        mockStepInputDto.setSequence(1);
        mockStepInputDto.setStepStartDate(LocalDate.of(2025, 1, 1));
        mockStepInputDto.setStepEndDate(LocalDate.of(2025, 1, 10));

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(1L);

        Step mockStep = new Step();
        mockStep.setStepId(1L);
        mockStep.setStepName("Test Step");

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.save(any(Step.class))).thenReturn(mockStep);
        doNothing().when(coachingProgramRepository).flush();

        Step result = stepService.addStepToProgram(mockStepInputDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Test Step", result.getStepName());

        Mockito.verify(stepRepository, Mockito.times(1)).save(any(Step.class));
        Mockito.verify(coachingProgramService, Mockito.times(1)).assignStepToCoachingProgram(eq(1L), any(Step.class));
        Mockito.verify(coachingProgramRepository, Mockito.times(1)).flush();
    }
    @Tag("unit")
    @Test
    void testUpdateStep_Success() {
        mockStepInputDto.setStepName("Updated Step");

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
        mockStepInputDto.setStepStartDate(LocalDate.parse("2025-01-01"));
        mockStepInputDto.setStepEndDate(LocalDate.parse("2025-01-10"));
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

        Mockito.verify(stepRepository, Mockito.times(1)).save(any(Step.class));
        Mockito.verify(validationHelper, Mockito.times(1)).validateSessions(sessionIds);
        Mockito.verify(validationHelper, Mockito.times(1)).validateAssignments(assignmentIds);
        Mockito. verify(coachingProgramService, Mockito.times(1)).updateProgramEndDate(anyLong());
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
    void testAssignAssignmentToStep_Success() {
        when(validationHelper.validateStep(1L)).thenReturn(mockStep);
        when(validationHelper.validateAssignment(1L)).thenReturn(mockAssignment);
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(mockAssignment);

        mockStep.setAssignment(new ArrayList<>());

        Step result = stepService.assignAssignmentToStep(1L, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getAssignment().contains(mockAssignment));
        Assertions.assertEquals(mockStep, mockAssignment.getStep());

        Mockito.verify(validationHelper, times(1)).validateStep(1L);
        Mockito.verify(validationHelper, times(1)).validateAssignment(1L);
        Mockito.verify(assignmentRepository, Mockito.times(1)).save(any(Assignment.class));
        Mockito.verify(stepRepository, Mockito.times(1)).save(any(Step.class));
    }

    @Tag("unit")
    @Test
    void testAssignAssignmentToStep_ThrowsException_WhenAssignmentAlreadyExists() {
        when(validationHelper.validateStep(1L)).thenReturn(mockStep);
        when(validationHelper.validateAssignment(1L)).thenReturn(mockAssignment);

        mockStep.getAssignment().add(mockAssignment);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            stepService.assignAssignmentToStep(1L, 1L);
        });

        assertEquals("Assignment is already associated with the step.", exception.getMessage());

        Mockito.verify(validationHelper, Mockito.times(1)).validateStep(1L);
        Mockito.verify(validationHelper, Mockito.times(1)).validateAssignment(1L);
        Mockito.verify(stepRepository, Mockito.never()).save(Mockito.any(Step.class));
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

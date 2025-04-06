package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.dtos.StepReorderDto;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.DateConverter;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.models.*;
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
import java.util.Collections;
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
    @Mock
    private Step mockStep;
    @Mock
    private StepInputDto mockStepInputDto;
    @Mock
    private Assignment mockAssignment;
    @Mock
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
    void returnsSteps_whenStepsExistForUserAndProgram() {
        String username = "testuser";
        Long programId = 1L;

        CoachingProgram mockProgram = new CoachingProgram();
        mockProgram.setCoachingProgramId(programId);

        Step mockStep = new Step();
        mockStep.setStepId(1L);
        mockStep.setStepName("Step 1");

        when(validationHelper.validateUser(username)).thenReturn(new User());
        when(coachingProgramRepository.findByCoachingProgramIdAndClientUsername(programId, username))
                .thenReturn(Optional.of(mockProgram));
        when(stepRepository.findByCoachingProgram(mockProgram)).thenReturn(List.of(mockStep));

        List<Step> result = stepService.getStepsForUserAndProgram(username, programId);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Step 1", result.get(0).getStepName());
    }

    @Tag("unit")
    @Test
    void throwsNotFound_whenProgramNotFoundForUser() {
        String username = "ghost";
        Long programId = 99L;

        when(validationHelper.validateUser(username)).thenReturn(new User());
        when(coachingProgramRepository.findByCoachingProgramIdAndClientUsername(programId, username))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                stepService.getStepsForUserAndProgram(username, programId));
    }

    @Tag("unit")
    @Test
    void throwsNotFound_whenNoStepsExistForProgram() {
        String username = "testuser";
        Long programId = 1L;

        CoachingProgram mockProgram = new CoachingProgram();
        mockProgram.setCoachingProgramId(programId);

        when(validationHelper.validateUser(username)).thenReturn(new User());
        when(coachingProgramRepository.findByCoachingProgramIdAndClientUsername(programId, username))
                .thenReturn(Optional.of(mockProgram));
        when(stepRepository.findByCoachingProgram(mockProgram)).thenReturn(List.of());

        Assertions.assertThrows(NotFoundException.class, () ->
                stepService.getStepsForUserAndProgram(username, programId));
    }



    @Tag("unit")
    @Test
    void testAddStepsToProgram_Success() {

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(validationHelper).validateStepCreationInput(any(StepInputDto.class));

        StepInputDto stepDto = new StepInputDto();
        stepDto.setCoachingProgramId(1L);
        stepDto.setStepName("Valid Step");
        stepDto.setSequence(1);
        stepDto.setStepStartDate(DateConverter.convertToDate(LocalDate.parse("2025-06-15")));
        stepDto.setStepEndDate(DateConverter.convertToDate(LocalDate.parse("2025-07-15")));

        Assertions.assertDoesNotThrow(() -> stepService.addStepsToProgram(List.of(stepDto)));

        Mockito.verify(validationHelper, Mockito.times(1)).validateCoachingProgram(1L);
        Mockito.verify(validationHelper, Mockito.times(1)).validateStepCreationInput(any(StepInputDto.class));
        Mockito.verify(stepRepository, Mockito.times(1)).save(any(Step.class));
        Mockito.verify(coachingProgramRepository, Mockito.times(1)).save(any(CoachingProgram.class));
        Mockito.verify(coachingProgramService, Mockito.times(1)).updateProgramEndDate(1L);
    }

    @Tag("unit")
    @Test
    void testAddMultipleStepsToProgram_Success() {
        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        doNothing().when(validationHelper).validateStepCreationInput(any(StepInputDto.class));
        when(stepRepository.findByCoachingProgram(any(CoachingProgram.class))).thenReturn(List.of());
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(coachingProgramRepository.save(any(CoachingProgram.class))).thenReturn(mockCoachingProgram);

        StepInputDto step1 = new StepInputDto();
        step1.setCoachingProgramId(1L);
        step1.setStepName("Step A");
        step1.setSequence(1);
        step1.setStepStartDate(DateConverter.convertToDate(LocalDate.parse("2025-01-01")));
        step1.setStepEndDate(DateConverter.convertToDate(LocalDate.parse("2025-02-01")));

        StepInputDto step2 = new StepInputDto();
        step2.setCoachingProgramId(1L);
        step2.setStepName("Step B");
        step2.setSequence(2);
        step2.setStepStartDate(DateConverter.convertToDate(LocalDate.parse("2025-02-02")));
        step2.setStepEndDate(DateConverter.convertToDate(LocalDate.parse("2025-03-01")));

        List<Step> result = stepService.addStepsToProgram(List.of(step1, step2));

        Assertions.assertEquals(2, result.size());
        verify(stepRepository, times(2)).save(any(Step.class));
        verify(coachingProgramRepository, times(2)).save(any(CoachingProgram.class));
        verify(coachingProgramService, times(2)).updateProgramEndDate(1L);
    }


    @Tag("unit")
    @Test
    void testAddStepsToProgram_WhenSequenceAlreadyExists() {
        Step existingStep = new Step();
        existingStep.setSequence(1);
        existingStep.setStepStartDate(LocalDate.parse("2025-05-01"));
        existingStep.setStepEndDate(LocalDate.parse("2025-09-01"));

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        doNothing().when(validationHelper).validateStepCreationInput(any(StepInputDto.class));

        doThrow(new IllegalArgumentException("A step with sequence 1 already exists in this program."))
                .when(validationHelper).validateStepSequence(eq(mockCoachingProgram), any(Step.class));

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
        Mockito.verify(stepRepository, never()).save(any(Step.class));
        Mockito.verify(coachingProgramRepository, never()).save(any(CoachingProgram.class));
    }

    @Tag("unit")
    @Test
    void testAddStepsToProgram_AutoIncrementsSequence_WhenNoneProvided() {
        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        doNothing().when(validationHelper).validateStepCreationInput(any(StepInputDto.class));
        when(stepRepository.findByCoachingProgram(any(CoachingProgram.class))).thenReturn(List.of());

        StepInputDto stepDto = new StepInputDto();
        stepDto.setCoachingProgramId(1L);
        stepDto.setStepName("Auto Step");
        stepDto.setSequence(null);
        stepDto.setStepStartDate(DateConverter.convertToDate(LocalDate.parse("2025-06-15")));
        stepDto.setStepEndDate(DateConverter.convertToDate(LocalDate.parse("2025-07-15")));

        Assertions.assertDoesNotThrow(() -> stepService.addStepsToProgram(List.of(stepDto)));

        verify(stepRepository).findByCoachingProgram(any(CoachingProgram.class));
    }



    @Tag("unit")
    @Test
    void testAddStepsToProgram_WhenStartDateConflictsWithSequence() {
        Step existingStep = new Step();
        existingStep.setStepId(99L);
        existingStep.setSequence(1);
        existingStep.setStepStartDate(LocalDate.of(2025, 7, 1));

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        lenient().when(stepRepository.findByCoachingProgram(mockCoachingProgram)).thenReturn(List.of());
        doNothing().when(validationHelper).validateStepCreationInput(any(StepInputDto.class));
        doThrow(new IllegalArgumentException("The step's start date conflicts with the provided sequence."))
                .when(validationHelper).validateStepSequence(eq(mockCoachingProgram), any(Step.class));

        StepInputDto newStepDto = new StepInputDto();
        newStepDto.setCoachingProgramId(1L);
        newStepDto.setStepName("Conflicting Start Date Step");
        newStepDto.setSequence(1);
        newStepDto.setStepStartDate(DateConverter.convertToDate(LocalDate.of(2025, 6, 20)));
        newStepDto.setStepEndDate(DateConverter.convertToDate(LocalDate.of(2025, 8, 20)));
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> stepService.addStepsToProgram(List.of(newStepDto)));

        assertEquals("The step's start date conflicts with the provided sequence.", exception.getMessage());
        verify(validationHelper).validateCoachingProgram(1L);
        verify(validationHelper).validateStepSequence(eq(mockCoachingProgram), any(Step.class));
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
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(coachingProgramRepository.save(any(CoachingProgram.class))).thenReturn(mockCoachingProgram);
        doNothing().when(validationHelper).validateStepCreationInput(any(StepInputDto.class));
        doNothing().when(validationHelper).validateStepSequence(eq(mockCoachingProgram), any(Step.class));

        StepInputDto newStepDto = new StepInputDto();
        newStepDto.setCoachingProgramId(1L);
        newStepDto.setStepName("Step with No Conflict");
        newStepDto.setSequence(3);
        newStepDto.setStepStartDate(DateConverter.convertToDate(LocalDate.of(2025, 7, 1)));
        newStepDto.setStepEndDate(DateConverter.convertToDate(LocalDate.of(2025, 8, 1)));

        Assertions.assertDoesNotThrow(() -> stepService.addStepsToProgram(List.of(newStepDto)));

        Mockito.verify(stepRepository, Mockito.times(1)).save(any(Step.class));
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

        Step result = stepService.updateStepDetails(1L, mockStepInputDto);

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
        mockStepInputDto.setStepGoal("New Goal");

        mockCoachingProgram.setCoachingProgramId(1L);
        mockStep.setCoachingProgram(mockCoachingProgram);

        when(validationHelper.validateStep(1L)).thenReturn(mockStep);
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Step result = stepService.updateStepDetails(1L, mockStepInputDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated Step", result.getStepName());
        Assertions.assertEquals(LocalDate.parse("2025-01-01"), result.getStepStartDate());
        Assertions.assertEquals(LocalDate.parse("2025-01-10"), result.getStepEndDate());
        Assertions.assertEquals("New Goal", result.getStepGoal());

        Mockito.verify(validationHelper, Mockito.times(1)).validateStep(1L);
        Mockito.verify(stepRepository, Mockito.times(1)).save(any(Step.class));
        Mockito.verify(coachingProgramService, Mockito.times(1)).updateProgramEndDate(1L);
    }

    @Tag("unit")
    @Test
    void testReorderStepSequence_Success() {
        StepReorderDto dto = new StepReorderDto();
        dto.setStepId(1L);
        dto.setNewSequence(3);

        mockStep.setStepId(1L);
        mockStep.setSequence(1);

        mockCoachingProgram.setCoachingProgramId(42L);
        mockStep.setCoachingProgram(mockCoachingProgram);

        when(validationHelper.validateStep(1L)).thenReturn(mockStep);
        doNothing().when(validationHelper).validateCoachOwnsProgramOrIsAdmin(mockCoachingProgram);
        doNothing().when(validationHelper).validateStepSequence(mockCoachingProgram, mockStep);
        when(stepRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Step> result = stepService.reorderStepSequence(List.of(dto));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(3, result.get(0).getSequence());

        verify(validationHelper).validateStep(1L);
        verify(validationHelper).validateCoachOwnsProgramOrIsAdmin(mockCoachingProgram);
        verify(validationHelper).validateStepSequence(mockCoachingProgram, mockStep);
        verify(stepRepository).saveAll(anyList());
    }

    @Tag("unit")
    @Test
    void testReorderStepSequence_NullInput_ThrowsException() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                stepService.reorderStepSequence(null));

        Assertions.assertEquals("No steps provided for reordering.", exception.getMessage());
    }

    @Tag("unit")
    @Test
    void testReorderStepSequence_EmptyInput_ThrowsException() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                stepService.reorderStepSequence(Collections.emptyList()));

        Assertions.assertEquals("No steps provided for reordering.", exception.getMessage());
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

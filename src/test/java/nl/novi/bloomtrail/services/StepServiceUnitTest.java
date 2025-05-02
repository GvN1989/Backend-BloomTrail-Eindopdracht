package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.exceptions.BadRequestException;
import nl.novi.bloomtrail.helper.StepSequenceHelper;
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
    private StepSequenceHelper stepSequenceHelper;
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

        mockStep = new Step();
        mockStep.setStepId(1L);
        mockStep.setCoachingProgram(mockCoachingProgram);

        mockStepInputDto = new StepInputDto();
        mockStepInputDto.setCoachingProgramId(1L);

        mockAssignment = new Assignment();
        mockAssignment .setAssignmentId(1L);


        lenient().when(validationHelper.validateStep(1L)).thenReturn(mockStep);
        lenient().when(validationHelper.validateAssignment(1L)).thenReturn(mockAssignment);
        lenient().doNothing().when(stepSequenceHelper).reorderStepsForProgram(mockCoachingProgram);
    }

    @Tag("unit")
    @Test
    void testFindStepById_Success() {
        when(validationHelper.validateStep(1L)).thenReturn(mockStep);

        Step result = stepService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getStepId());

        Mockito.verify(validationHelper, times(1)).validateStep(1L);
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
    void testReturnsStepsLinkedToUserAndProgram() {
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

        assertEquals(1, result.size());
        assertEquals("Step 1", result.get(0).getStepName());
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
    void testAddStepToProgram_Success() {

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(validationHelper).validateStepCreationInput(any(StepInputDto.class));

        StepInputDto stepDto = new StepInputDto();
        stepDto.setCoachingProgramId(1L);
        stepDto.setStepName("Valid Step");
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
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(coachingProgramRepository.save(any(CoachingProgram.class))).thenReturn(mockCoachingProgram);

        StepInputDto step1 = new StepInputDto();
        step1.setCoachingProgramId(1L);
        step1.setStepName("Step A");
        step1.setStepStartDate(DateConverter.convertToDate(LocalDate.parse("2025-01-01")));
        step1.setStepEndDate(DateConverter.convertToDate(LocalDate.parse("2025-02-01")));

        StepInputDto step2 = new StepInputDto();
        step2.setCoachingProgramId(1L);
        step2.setStepName("Step B");
        step2.setStepStartDate(DateConverter.convertToDate(LocalDate.parse("2025-02-02")));
        step2.setStepEndDate(DateConverter.convertToDate(LocalDate.parse("2025-03-01")));

        List<Step> result = stepService.addStepsToProgram(List.of(step1, step2));

        Assertions.assertEquals(2, result.size());
        verify(stepRepository, times(2)).save(any(Step.class));
        verify(coachingProgramRepository, times(1)).save(any(CoachingProgram.class));
        verify(coachingProgramService, times(1)).updateProgramEndDate(1L);
    }

    @Tag("unit")
    @Test
    void testAddStepsToProgram_ThrowsBadRequest_WhenInputDtoIsEmpty() {
        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> stepService.addStepsToProgram(new ArrayList<>())
        );

        assertEquals("Step input list must not be empty.", exception.getMessage());
    }

    @Tag("unit")
    @Test
    void testAddStepsToProgram_ThrowsBadRequest_WhenNameOrStartDateIsMissing() {
        StepInputDto invalidDto = new StepInputDto();
        invalidDto.setCoachingProgramId(1L);

        List<StepInputDto> inputList = List.of(invalidDto);

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);

        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> stepService.addStepsToProgram(inputList)
        );

        assertEquals("Step name and start date are required.", exception.getMessage());
    }


    @Tag("unit")
    @Test
    void testUpdateStep_Success() {
        mockStepInputDto.setStepName("Updated Step");

        when(validationHelper.validateStep(1L)).thenReturn(mockStep);
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Step result = stepService.updateStepDetails(1L, mockStepInputDto);

        assertNotNull(result);
        assertEquals("Updated Step", result.getStepName());
        verify(stepRepository, times(1)).save(any(Step.class));
        verify(coachingProgramService, times(1)).updateProgramEndDate(1L);
        verify(stepSequenceHelper, times(1)).reorderStepsForProgram(mockCoachingProgram);
    }

    @Tag("unit")
    @Test
    void testUpdateStep_CoversAllFields() {
        mockStepInputDto.setStepName("Updated Step");
        mockStepInputDto.setStepStartDate(DateConverter.convertToDate(LocalDate.parse("2025-01-01")));
        mockStepInputDto.setStepEndDate(DateConverter.convertToDate(LocalDate.parse("2025-01-10")));
        mockStepInputDto.setStepGoal("New Goal");
        mockStepInputDto.setCompleted(true);

        mockCoachingProgram.setCoachingProgramId(1L);
        mockStep.setCoachingProgram(mockCoachingProgram);

        when(validationHelper.validateStep(1L)).thenReturn(mockStep);
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Step result = stepService.updateStepDetails(1L, mockStepInputDto);

        assertNotNull(result);
        assertEquals("Updated Step", result.getStepName());
        assertEquals(LocalDate.parse("2025-01-01"), result.getStepStartDate());
        assertEquals(LocalDate.parse("2025-01-10"), result.getStepEndDate());
        assertEquals("New Goal", result.getStepGoal());
        Assertions.assertTrue(result.getCompleted());

        verify(validationHelper, times(1)).validateStep(1L);
        verify(stepRepository, times(1)).save(any(Step.class));
        verify(coachingProgramService, times(1)).updateProgramEndDate(1L);
    }

    @Tag("unit")
    @Test
    void testDeleteStep_Success() {

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(1L);
        mockStep.setCoachingProgram(mockCoachingProgram);

        when(validationHelper.validateStep(1L)).thenReturn(mockStep);

        stepService.deleteStep(1L);

        verify(validationHelper, times(1)).validateStep(1L);
        verify(coachingProgramService, times(1)).updateProgramEndDate(1L);
        verify(stepRepository, times(1)).delete(mockStep);
    }

    @Tag("unit")
    @Test
    void testDownloadFilesForStep_Success() throws IOException {
        when(validationHelper.validateStep(1L)).thenReturn(mockStep);

        byte[] mockData = "mock file data".getBytes();
        when(downloadService.downloadFilesForEntity(any(Step.class))).thenReturn(mockData);

        byte[] result = stepService.downloadFilesForStep(1L);

        assertNotNull(result);
        assertEquals(mockData.length, result.length);

        verify(validationHelper, times(1)).validateStep(1L);
        verify(downloadService, times(1)).downloadFilesForEntity(mockStep);
    }



}

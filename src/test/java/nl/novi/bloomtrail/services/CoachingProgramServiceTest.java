package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.SimpleCoachingProgramDto;
import nl.novi.bloomtrail.exceptions.EntityNotFoundException;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoachingProgramServiceTest {

    @Mock
    private CoachingProgramRepository coachingProgramRepository;

    @Mock
    private EntityValidationHelper validationHelper;

    @Mock
    private StepRepository stepRepository;

    @Mock
    private StrengthResultsRepository strengthResultsRepository;

    @InjectMocks
    private CoachingProgramService coachingProgramService;

    private CoachingProgram mockCoachingProgram;

    @BeforeEach
    public void setUp() {
        mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(1L);
        mockCoachingProgram.setTimeline(new ArrayList<>());
    }

    @Tag("unit")
    @Test
    public void testFindByID_SUCCES() {

        Long coachingProgramId = 6L;

        CoachingProgram mockProgram = new CoachingProgram();
        mockProgram.setCoachingProgramId(coachingProgramId);
        mockProgram.setGoal("Test Goal");

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(mockProgram);

        CoachingProgram result = coachingProgramService.findById(coachingProgramId);

        Assertions.assertNotNull(result, "The result should not be null");
        Assertions.assertEquals(coachingProgramId, result.getCoachingProgramId(), "The coaching program ID should match");
        Assertions.assertEquals("Test Goal", result.getGoal(), "The coaching program goal should match");

        Mockito.verify(validationHelper, Mockito.times(1)).validateCoachingProgram(coachingProgramId);
    }

    @Tag("unit")
    @Test
    public void testFindById_NotFound() {

        Long coachingProgramId = 6L;

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenThrow(new EntityNotFoundException("CoachingProgram" , coachingProgramId));

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            coachingProgramService.findById(coachingProgramId);
        });

        Assertions.assertEquals("CoachingProgram with ID 6 not found. ", exception.getMessage(), "Exception message should match");
    }

    @Tag("unit")
    @Test
    public void testGetCoachingProgramDetails_SUCCES() {
        String clientUsername = "testClient";
        String coachUsername = "testCoach";
        SimpleCoachingProgramDto mockDto1 = new SimpleCoachingProgramDto(1L, "Let's Go", clientUsername, coachUsername);
        SimpleCoachingProgramDto mockDto2 = new SimpleCoachingProgramDto(2L, "Keep Moving", clientUsername, coachUsername);

        List<SimpleCoachingProgramDto> mockDtos = Arrays.asList(mockDto1, mockDto2);

        when(coachingProgramRepository.findAllCoachingProgramDetails()).thenReturn(mockDtos);

        List<SimpleCoachingProgramDto> result = coachingProgramService.getCoachingProgramDetails();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Let's Go", result.get(0).getCoachingProgramName());
        Assertions.assertEquals("Keep Moving", result.get(1).getCoachingProgramName());
        Assertions.assertEquals(clientUsername, result.get(0).getClientUsername());
        Assertions.assertEquals(coachUsername, result.get(0).getCoachUsername());
    }
    @Tag("unit")
    @Test
    public void testGetCoachingProgramDetails_NotFound_EmptyList() {

        when(coachingProgramRepository.findAllCoachingProgramDetails()).thenReturn(Collections.emptyList());

        List<SimpleCoachingProgramDto> result = coachingProgramService.getCoachingProgramDetails();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Tag("unit")
    @Test
    public void testDeleteCoachingProgram_SUCCES() {
        Long coachingProgramId = 6L;

        CoachingProgram MockCoachingProgram = new CoachingProgram();
        MockCoachingProgram.setCoachingProgramId(coachingProgramId);

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(MockCoachingProgram);

        coachingProgramService.deleteCoachingProgram(coachingProgramId);

        Mockito.verify(validationHelper, Mockito.times(1)).validateCoachingProgram(coachingProgramId);
        Mockito.verify(coachingProgramRepository, Mockito.times(1)).delete(MockCoachingProgram);
    }

    @Tag("unit")
    @Test
    public void testDeleteCoachingProgram_NotFound() {
        Long coachingProgramId = 6L;

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenThrow(new RecordNotFoundException("CoachingProgram with ID " + coachingProgramId + " not found."));

        RecordNotFoundException exception = Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.deleteCoachingProgram(coachingProgramId);
        });

        Assertions.assertEquals("CoachingProgram with ID 6 not found.", exception.getMessage());

        Mockito.verify(coachingProgramRepository, Mockito.never()).deleteById(Mockito.any());
    }

    @Test
    @Tag("unit")
    public void testAssignStepToCoachingProgram_SUCCES() {

        Long coachingProgramId = 1L;

        Step newStep = new Step();
        newStep.setSequence(1);
        newStep.setStepStartDate(Date.from(
                LocalDate.parse("2025-01-10").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        newStep.setStepEndDate(Date.from(
                LocalDate.parse("2025-01-15").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setTimeline(new ArrayList<>());

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(mockCoachingProgram);
        when(stepRepository.findStepsByCoachingProgram(coachingProgramId)).thenReturn(new ArrayList<>());
        when(stepRepository.save(Mockito.any(Step.class))).thenReturn(newStep);

        CoachingProgram result = coachingProgramService.assignStepToCoachingProgram(coachingProgramId, newStep);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertTrue(result.getTimeline().contains(newStep), "New step should be added to the timeline");
        Assertions.assertEquals(1, result.getTimeline().size(),"Timeline size should be 1");

        Mockito.verify(validationHelper, Mockito.times(2)).validateCoachingProgram(coachingProgramId);
        Mockito.verify(stepRepository, Mockito.times(1)).findStepsByCoachingProgram(coachingProgramId);
        Mockito.verify(stepRepository, Mockito.times(1)).save(newStep);
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_DuplicateStep(){
        Step existingStep = new Step();
        existingStep.setStepId(1L);
        existingStep.setStepStartDate(Date.from(
                LocalDate.parse("2025-01-10").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Step duplicateStep = new Step();
        duplicateStep.setStepId(1L);
        duplicateStep.setStepStartDate(Date.from(
                LocalDate.parse("2025-01-15").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(1L);
        mockCoachingProgram.setTimeline(new ArrayList<>(List.of(existingStep)));

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.assignStepToCoachingProgram(1L, duplicateStep);
        });

        Assertions.assertEquals("Step is already part of the coaching program timeline.", exception.getMessage());

        Mockito.verify(stepRepository, Mockito.never()).save(Mockito.any(Step.class));
    }

    @Test
    @Tag("unit")
    public void testAssignStepToCoachingProgram_SequenceConflict() {
        Step existingStep = new Step();
        existingStep.setSequence(1);
        existingStep.setStepStartDate(Date.from(
                LocalDate.parse("2025-01-10").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Step conflictingStep = new Step();
        conflictingStep.setSequence(1);
        conflictingStep.setStepStartDate(Date.from(
                LocalDate.parse("2025-01-11").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.findStepsByCoachingProgram(1L)).thenReturn(List.of(existingStep));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.assignStepToCoachingProgram(1L, conflictingStep);
        });

        Assertions.assertEquals("A step with sequence 1 already exists in this program.", exception.getMessage());

        Mockito.verify(stepRepository, Mockito.never()).save(Mockito.any(Step.class));
    }

    @Test
    @Tag("unit")
    public void testAssignStepToCoachingProgram_StartDateConflict() {
        Step existingStep = new Step();
        existingStep.setSequence(1);
        existingStep.setStepStartDate(Date.from(
                LocalDate.parse("2025-01-10").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Step conflictingStep = new Step();
        conflictingStep.setSequence(2);
        conflictingStep.setStepStartDate(Date.from(
                LocalDate.parse("2025-01-01").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.findStepsByCoachingProgram(1L)).thenReturn(List.of(existingStep));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.assignStepToCoachingProgram(1L, conflictingStep);
        });

        Assertions.assertEquals("The step's start date conflicts with the provided sequence.", exception.getMessage());

        Mockito.verify(stepRepository, Mockito.never()).save(Mockito.any(Step.class));
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_CoachingProgramNotFound() {

        Long invalidCoachingProgramId = 999L;
        Step newStep = new Step();
        newStep.setSequence(1);
        newStep.setStepStartDate(Date.from(
                LocalDate.parse("2025-01-01").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(validationHelper.validateCoachingProgram(invalidCoachingProgramId))
                .thenThrow(new EntityNotFoundException("CoachingProgram", invalidCoachingProgramId));

              EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
                  coachingProgramService.assignStepToCoachingProgram(invalidCoachingProgramId, newStep);
              });

        Assertions.assertEquals("CoachingProgram with ID 999 not found. ", exception.getMessage());

        Mockito.verify(stepRepository, Mockito.never()).save(Mockito.any(Step.class));
    }
    @Tag ("unit")
    @Test
    public void testCalculateProgressPercentage_SUCCES() {

        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);

        Step step1 = new Step();
        step1.setStepId(1L);
        step1.setCompleted(true);

        Step step2 = new Step();
        step2.setStepId(2L);
        step2.setCompleted(false);

        Step step3 = new Step();
        step3.setStepId(3L);
        step3.setCompleted(true);

        mockCoachingProgram.setTimeline(Arrays.asList(step1, step2, step3));

        Mockito.when(validationHelper.validateCoachingProgram(coachingProgramId))
                .thenReturn(mockCoachingProgram);


        double progressPercentage = coachingProgramService.calculateProgressPercentage(coachingProgramId);

        Assertions.assertEquals(66.67, progressPercentage, 0.01);

        Mockito.verify(validationHelper, Mockito.times(1)).validateCoachingProgram(coachingProgramId);
        Mockito.verify(coachingProgramRepository, Mockito.times(1)).save(mockCoachingProgram);
    }

    @Tag ("unit")
    @Test
    public void testCalculateProgressPercentage_EmptyTimeline() {

        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setTimeline(Collections.emptyList());

        Mockito.when(validationHelper.validateCoachingProgram(coachingProgramId))
                .thenReturn(mockCoachingProgram);

        double progressPercentage = coachingProgramService.calculateProgressPercentage(coachingProgramId);

        Assertions.assertEquals(0.0, progressPercentage);

        Mockito.verify(validationHelper, Mockito.times(1)).validateCoachingProgram(coachingProgramId);
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }

    @Tag ("unit")
    @Test
    public void testCalculateProgressPercentage_AllStepsCompleted() {

        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);

        Step step1 = new Step();
        step1.setStepId(1L);
        step1.setCompleted(true);

        Step step2 = new Step();
        step2.setStepId(2L);
        step2.setCompleted(true);

        mockCoachingProgram.setTimeline(Arrays.asList(step1, step2));

        Mockito.when(validationHelper.validateCoachingProgram(coachingProgramId))
                .thenReturn(mockCoachingProgram);

        double progressPercentage = coachingProgramService.calculateProgressPercentage(coachingProgramId);

        Assertions.assertEquals(100.0, progressPercentage);

        Mockito.verify(validationHelper, Mockito.times(1)).validateCoachingProgram(coachingProgramId);
        Mockito.verify(coachingProgramRepository, Mockito.times(1)).save(mockCoachingProgram);
    }

    @Tag("unit")
    @Test
    public void testCalculateProgressPercentage_AfterStepStatusChange() {
        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);

        Step step1 = new Step();
        step1.setStepId(1L);
        step1.setCompleted(true);

        Step step2 = new Step();
        step2.setStepId(2L);
        step2.setCompleted(true);

        Step step3 = new Step();
        step3.setStepId(3L);
        step3.setCompleted(false);

        mockCoachingProgram.setTimeline(Arrays.asList(step1, step2, step3));

        Mockito.when(validationHelper.validateCoachingProgram(coachingProgramId))
                .thenReturn(mockCoachingProgram);

        double initialProgressPercentage = coachingProgramService.calculateProgressPercentage(coachingProgramId);

        Assertions.assertEquals(66.67, initialProgressPercentage, 0.01);

        step3.setCompleted(true);

        double updatedProgressPercentage = coachingProgramService.calculateProgressPercentage(coachingProgramId);

        Assertions.assertEquals(100.0, updatedProgressPercentage, 0.01);

        Mockito.verify(validationHelper, Mockito.times(2)).validateCoachingProgram(coachingProgramId);
        Mockito.verify(coachingProgramRepository, Mockito.times(2)).save(mockCoachingProgram);
    }

    @Tag("unit")
    @Test
    public void testAssignCoachingResultsToCoachingProgram_SUCCESS(){

        Long coachingProgramId = 1L;
        List<Long> strengthResultsIds = List.of(100L, 101L);

        StrengthResults result1 = new StrengthResults();
        result1.setResultsId(100L);

        StrengthResults result2 = new StrengthResults();
        result2.setResultsId(101L);

        List<StrengthResults> strengthResultsList = List.of(result1, result2);

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(mockCoachingProgram);
        when(strengthResultsRepository.findAllById(strengthResultsIds)).thenReturn(strengthResultsList);

        CoachingProgram result = coachingProgramService.assignStrengthResultsToCoachingProgram(coachingProgramId, strengthResultsIds);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockCoachingProgram, result);
        Mockito.verify(strengthResultsRepository, Mockito.times(1)).saveAll(strengthResultsList);
        Assertions.assertEquals(mockCoachingProgram, result1.getCoachingProgram());
        Assertions.assertEquals(mockCoachingProgram, result2.getCoachingProgram());
    }

    @Tag("unit")
    @Test
    public void testAssignCoachingResultsToCoachingProgram_NoStrengthResultsFound(){
        Long coachingProgramId = 1L;
        List<Long> invalidStrengthResultsIds = List.of(999L, 1000L);

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(mockCoachingProgram);
        when(strengthResultsRepository.findAllById(invalidStrengthResultsIds)).thenReturn(Collections.emptyList());

        RecordNotFoundException exception = Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.assignStrengthResultsToCoachingProgram(coachingProgramId, invalidStrengthResultsIds);
        });

        Assertions.assertEquals("No StrengthResults found for the provided IDs: [999, 1000]", exception.getMessage());

        Mockito.verify(strengthResultsRepository, Mockito.never()).saveAll(Mockito.anyList());
    }

    @Test
    @Tag("unit")
    public void testAssignStrengthResultsToCoachingProgram_CoachingProgramNotFound() {
        Long invalidCoachingProgramId = 999L;
        List<Long> strengthResultsIds = List.of(100L);

        when(validationHelper.validateCoachingProgram(invalidCoachingProgramId))
                .thenThrow(new EntityNotFoundException("CoachingProgram", invalidCoachingProgramId));


        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            coachingProgramService.assignStrengthResultsToCoachingProgram(invalidCoachingProgramId, strengthResultsIds);
        });

        Assertions.assertEquals("CoachingProgram with ID 999 not found. ", exception.getMessage());


        Mockito.verify(strengthResultsRepository, Mockito.never()).findAllById(Mockito.anyList());
        Mockito.verify(strengthResultsRepository, Mockito.never()).saveAll(Mockito.anyList());
    }

    @Tag("unit")
    @Test
    public void testUpdateProgramEndDate_WithSteps() {
        Long coachingProgramId = 1L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);

        Step step1 = new Step();
        step1.setStepEndDate(Date.from(LocalDate.parse("2025-01-10").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Step step2 = new Step();
        step2.setStepEndDate(Date.from(LocalDate.parse("2025-01-15").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        mockCoachingProgram.setTimeline(List.of(step1, step2));
        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(mockCoachingProgram);

        coachingProgramService.updateProgramEndDate(coachingProgramId);

        Assertions.assertEquals(
                Date.from(LocalDate.parse("2025-01-15").atStartOfDay(ZoneId.systemDefault()).toInstant()),
                mockCoachingProgram.getEndDate()
        );

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).save(mockCoachingProgram);
    }


    @Test
    @Tag("unit")
    public void testUpdateProgramEndDate_EmptyTimeline() {
        Long coachingProgramId = 1L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setTimeline(new ArrayList<>());

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(mockCoachingProgram);

        coachingProgramService.updateProgramEndDate(coachingProgramId);

        Assertions.assertNull(mockCoachingProgram.getEndDate());
        Mockito.verify(coachingProgramRepository, Mockito.times(1)).save(mockCoachingProgram);
    }

    @Test
    @Tag("unit")
    public void testUpdateProgramEndDate_InvalidStepEndDates() {
        Long coachingProgramId = 1L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);

        Step step1 = new Step();
        step1.setStepEndDate(null);

        mockCoachingProgram.setTimeline(List.of(step1));
        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(mockCoachingProgram);

        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            coachingProgramService.updateProgramEndDate(coachingProgramId);
        });

        Assertions.assertEquals("No valid step end dates found.", exception.getMessage());
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }


}

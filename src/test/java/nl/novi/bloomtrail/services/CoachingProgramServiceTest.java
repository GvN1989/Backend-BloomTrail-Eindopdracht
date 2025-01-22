package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoachingProgramServiceTest {

    @Mock
    private CoachingProgramRepository coachingProgramRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private StepRepository stepRepository;

    @Mock
    private StrengthResultsRepository strengthResultsRepository;

    @InjectMocks
    private CoachingProgramService coachingProgramService;

    @Tag("unit")
    @Test
    public void testFindByID_SUCCES() {
        CoachingProgram mockProgram = new CoachingProgram();
        mockProgram.setCoachingProgramId(6L);
        mockProgram.setGoal("Test Goal");

        when(coachingProgramRepository.findById(6L)).thenReturn(Optional.of(mockProgram));

        CoachingProgram result = coachingProgramService.findById(6L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(6L, result.getCoachingProgramId());
        Assertions.assertEquals("Test Goal", result.getGoal());
    }

    @Tag("unit")
    @Test
    public void testFindById_NotFound() {
        when(coachingProgramRepository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> {
            coachingProgramService.findById(1L);
        });
    }

    @Tag("unit")
    @Test
    public void testFindByUser_SUCCES() {
        String username = "testUser";

        CoachingProgram program1 = new CoachingProgram();
        program1.setCoachingProgramId(1L);
        program1.setGoal("Goal 1");

        CoachingProgram program2 = new CoachingProgram();
        program2.setCoachingProgramId(2L);
        program2.setGoal("Goal 2");

        List<CoachingProgram> mockPrograms = Arrays.asList(program1, program2);

        when(coachingProgramRepository.findByUsername(username)).thenReturn(mockPrograms);

        List<CoachingProgram> result = coachingProgramService.findByUser(username);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Goal 1", result.get(0).getGoal());
        Assertions.assertEquals("Goal 2", result.get(1).getGoal());
    }
    @Tag("unit")
    @Test
    public void testFindByUser_NotFound() {
        String username = "nonExistentUser";

        when(coachingProgramRepository.findByUsername(username)).thenReturn(List.of());

        Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.findByUser(username);
        });

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findByUsername(username);
    }

    @Tag("unit")
    @Test
    public void testDeleteCoachingProgram_SUCCES() {
        Long CoachingProgramId = 6L;

        when(coachingProgramRepository.existsById(CoachingProgramId)).thenReturn(true);

        coachingProgramService.deleteCoachingProgram(CoachingProgramId);

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).deleteById(CoachingProgramId);
    }

    @Tag("unit")
    @Test
    public void testDeleteCoachingProgram_NotFound() {
        Long CoachingProgramId = 2L;
        when(coachingProgramRepository.existsById(CoachingProgramId)).thenReturn(false);

        Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.deleteCoachingProgram(CoachingProgramId);
        });
        Mockito.verify(coachingProgramRepository, Mockito.never()).deleteById(Mockito.any());
    }

    @Tag("unit")
    @Test
    public void testAssignUserToCoachingProgram_SUCCES() {

        String username = "testUser";
        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setGoal("Test Goal");

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setFullName("Jose Suarez");

        when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));
        when(userRepository.findById(username)).thenReturn(Optional.of(mockUser));
        when(coachingProgramRepository.save(Mockito.any(CoachingProgram.class))).thenReturn(mockCoachingProgram);

        CoachingProgram result = coachingProgramService.assignUserToCoachingProgram(coachingProgramId, username);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockUser, result.getUser());
        Assertions.assertEquals(username, result.getUser().getUsername());

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
        Mockito.verify(userRepository, Mockito.times(1)).findById(username);
        Mockito.verify(coachingProgramRepository, Mockito.times(1)).save(mockCoachingProgram);

        Assertions.assertEquals(mockUser, mockCoachingProgram.getUser());
    }

    @Tag("unit")
    @Test
    public void testAssignUserToCoachingProgram_NotFound_CoachingProgram() {

        String username = "testUser";
        Long coachingProgramId = 6L;

        when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.empty());

        Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.assignUserToCoachingProgram(coachingProgramId, username);
        });

        Mockito.verify(coachingProgramRepository, Mockito.times(1))
                .findById(coachingProgramId);
        Mockito.verify(userRepository, Mockito.never()).findById(username);
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());

    }

    @Tag("unit")
    @Test
    public void testAssignUserToCoachingProgram_NotFound_User() {

        String username = "testUser";
        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);


        when(coachingProgramRepository.findById(coachingProgramId))
                .thenReturn(Optional.of(mockCoachingProgram));
        when(userRepository.findById(username))
                .thenReturn(Optional.empty());


        Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.assignUserToCoachingProgram(coachingProgramId, username);
        });

        Mockito.verify(coachingProgramRepository, Mockito.times(1))
                .findById(coachingProgramId);
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(username);
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_SUCCES() {

        Long coachingProgramId = 6L;
        Long stepId = 4L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setGoal("Test Goal");

        Step newStep = new Step();
        newStep.setStepId(stepId);
        newStep.setStepName("New Step");

        when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));
        when(stepRepository.findById(stepId)).thenReturn(Optional.of(newStep));
        when(coachingProgramRepository.save(Mockito.any(CoachingProgram.class))).thenReturn(mockCoachingProgram);

        CoachingProgram result = coachingProgramService.assignStepToCoachingProgram(coachingProgramId, stepId);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getTimeline().contains(newStep));
        Assertions.assertEquals(1, result.getTimeline().size());

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
        Mockito.verify(stepRepository, Mockito.times(1)).findById(stepId);
        Mockito.verify(coachingProgramRepository, Mockito.times(1)).save(Mockito.any(CoachingProgram.class));

    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_DuplicateStepObjects(){

        Long coachingProgramId = 6L;
        Long stepId = 4L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setGoal("Test Goal");

        Step existingStep = new Step();
        existingStep.setStepId(4L);
        existingStep.setStepName("Step one");

        Step newStep = new Step();
        newStep.setStepId(stepId);
        newStep.setStepName("Step two");

        mockCoachingProgram.getTimeline().add(newStep);

        when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));
        when(stepRepository.findById(stepId)).thenReturn(Optional.of(newStep));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.assignStepToCoachingProgram(coachingProgramId, stepId);
        });

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
        Mockito.verify(stepRepository, Mockito.times(1)).findById(stepId);
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_DuplicateStepName(){

        Long coachingProgramId = 6L;
        Long stepId = 4L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setGoal("Test Goal");

        Step existingStep = new Step();
        existingStep.setStepId(1L);
        existingStep.setStepName("Step one");

        Step newStep = new Step();
        newStep.setStepId(stepId);
        newStep.setStepName("Step one");

        mockCoachingProgram.getTimeline().add(existingStep);

        when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));
        when(stepRepository.findById(stepId)).thenReturn(Optional.of(newStep));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.assignStepToCoachingProgram(coachingProgramId, stepId);
        });

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
        Mockito.verify(stepRepository, Mockito.times(1)).findById(stepId);
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_StepNotFound() {

        Long coachingProgramId = 6L;
        Long stepId = 4L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);

        when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));
        when(stepRepository.findById(stepId)).thenReturn(Optional.empty());


        Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.assignStepToCoachingProgram(coachingProgramId, stepId);
        });

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
        Mockito.verify(stepRepository, Mockito.times(1)).findById(stepId);
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_CoachingProgramNotFound() {

        Long coachingProgramId = 6L;
        Long stepId = 4L;

        when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.empty());

              Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.assignStepToCoachingProgram(coachingProgramId, stepId);
        });

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
        Mockito.verify(stepRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }
    @Tag ("unit")
    @Test
    public void testCalculateProgressPercentage_SUCCES() {

        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId (coachingProgramId);

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

        Mockito.when(coachingProgramRepository.findById(coachingProgramId))
                        .thenReturn(Optional.of(mockCoachingProgram));

        double progressPercentage = coachingProgramService.calculateProgressPercentage(coachingProgramId);

        Assertions.assertEquals(66.67, progressPercentage, 0.01);

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);

    }

    @Tag ("unit")
    @Test
    public void testCalculateProgressPercentage_EmptyTimeline() {

        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setTimeline(Collections.emptyList());

        Mockito.when(coachingProgramRepository.findById(coachingProgramId))
                .thenReturn(Optional.of(mockCoachingProgram));

        double progressPercentage = coachingProgramService.calculateProgressPercentage(coachingProgramId);

        Assertions.assertEquals(0.0, progressPercentage);

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);

    }

    @Tag ("unit")
    @Test
    public void testCalculateProgressPercentage_AllStepsCompleted() {

        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId (coachingProgramId);

        Step step1 = new Step();
        step1.setStepId(1L);
        step1.setCompleted(true);

        Step step2 = new Step();
        step2.setStepId(2L);
        step2.setCompleted(true);

        mockCoachingProgram.setTimeline(Arrays.asList(step1, step2));

        Mockito.when(coachingProgramRepository.findById(coachingProgramId))
                .thenReturn(Optional.of(mockCoachingProgram));

        double progressPercentage = coachingProgramService.calculateProgressPercentage(coachingProgramId);

        Assertions.assertEquals(100.0, progressPercentage);

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);

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

        Mockito.when(coachingProgramRepository.findById(coachingProgramId))
                .thenReturn(Optional.of(mockCoachingProgram));

        double initialProgressPercentage = coachingProgramService.calculateProgressPercentage(coachingProgramId);

        Assertions.assertEquals(66.67, initialProgressPercentage, 0.01);

        step3.setCompleted(true);

        double updatedProgressPercentage = coachingProgramService.calculateProgressPercentage(coachingProgramId);

        Assertions.assertEquals(100.0, updatedProgressPercentage, 0.01);

        Mockito.verify(coachingProgramRepository, Mockito.times(2)).findById(coachingProgramId);
    }

    @Tag("unit")
    @Test
    public void testAssignCoachingResultsToCoachingProgram_SUCCESS(){

        Long coachingProgramId= 6L;
        Long strengthResultsId= 2L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);

        StrengthResults mockStrengthResults = new StrengthResults();
        mockStrengthResults.setResultsId(strengthResultsId);

        Mockito.when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));
        Mockito.when(strengthResultsRepository.findById(strengthResultsId)).thenReturn(Optional.of(mockStrengthResults));
        Mockito.when(coachingProgramRepository.save(Mockito.any(CoachingProgram.class))).thenReturn(mockCoachingProgram);

        CoachingProgram result = coachingProgramService.assignCoachingResultsToCoachingProgram(coachingProgramId, strengthResultsId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockStrengthResults, result.getStrengthResults());

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
        Mockito.verify(strengthResultsRepository, Mockito.times(1)).findById(strengthResultsId);
        Mockito.verify(coachingProgramRepository, Mockito.times(1)).save(mockCoachingProgram);
    }

    @Tag("unit")
    @Test
    public void testAssignCoachingResultsToCoachingProgram_StrengthResultsNotFound(){
        Long coachingProgramId= 6L;
        Long strengthResultsId= 2L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);

        Mockito.when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));
        Mockito.when(strengthResultsRepository.findById(strengthResultsId)).thenReturn(Optional.empty());

        Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.assignCoachingResultsToCoachingProgram(coachingProgramId, strengthResultsId);
        });

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
        Mockito.verify(strengthResultsRepository, Mockito.times(1)).findById(strengthResultsId);
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }
    @Tag("unit")
    @Test
    public void testUpdateProgramEndDate_Success() {
        Long coachingId = 1L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingId);
        mockCoachingProgram.setEndDate(new Date(2025 - 1900, 1, 1)); // January 1, 2025

        Step step1 = new Step();
        step1.setStepEndDate(new Date(2025 - 1900, 1, 10)); // January 10, 2025

        Step step2 = new Step();
        step2.setStepEndDate(new Date(2025 - 1900, 1, 15)); // January 15, 2025

        mockCoachingProgram.setTimeline(Arrays.asList(step1, step2));

        Mockito.when(coachingProgramRepository.findById(coachingId)).thenReturn(Optional.of(mockCoachingProgram));
        Mockito.when(coachingProgramRepository.save(Mockito.any(CoachingProgram.class))).thenReturn(mockCoachingProgram);

        coachingProgramService.updateProgramEndDate(coachingId);

        Assertions.assertEquals(new Date(2025 - 1900, 1, 15), mockCoachingProgram.getEndDate());

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).save(mockCoachingProgram);
    }

    @Tag("unit")
    @Test
    public void testUpdateProgramEndDate_NoSteps() {
        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setEndDate(new Date(2025 - 1900, 1, 1)); // January 1, 2025

        mockCoachingProgram.setTimeline(Collections.emptyList()); // No steps in timeline

        Mockito.when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));

        coachingProgramService.updateProgramEndDate(coachingProgramId);

        Assertions.assertEquals(new Date(2025 - 1900, 1, 1), mockCoachingProgram.getEndDate());

        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }

    @Tag("unit")
    @Test
    public void testUpdateProgramEndDate_CoachingProgramNotFound() {
        Long coachingProgramId = 6L;

        Mockito.when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.empty());

        Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.updateProgramEndDate(coachingProgramId);
        });

        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }

    @Tag("unit")
    @Test
    public void testUpdateProgramEndDate_LatestEndDateEqualsEndDate() {
        Long coachingProgramId = 1L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setEndDate(new Date(2025 - 1900, 1, 10));

        Step step1 = new Step();
        step1.setStepEndDate(new Date(2025 - 1900, 1, 10));

        mockCoachingProgram.setTimeline(List.of(step1));

        Mockito.when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));

        coachingProgramService.updateProgramEndDate(coachingProgramId);

        Assertions.assertEquals(new Date(2025 - 1900, 1, 10), mockCoachingProgram.getEndDate());
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }

    @Tag("unit")
    @Test
    public void testUpdateProgramEndDate_LatestEndDateBeforeEndDate() {
        Long coachingId = 1L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingId);
        mockCoachingProgram.setEndDate(new Date(2025 - 1900, 1, 10));

        Step step1 = new Step();
        step1.setStepEndDate(new Date(2025 - 1900, 1, 5));

        mockCoachingProgram.setTimeline(List.of(step1));

        Mockito.when(coachingProgramRepository.findById(coachingId)).thenReturn(Optional.of(mockCoachingProgram));

        coachingProgramService.updateProgramEndDate(coachingId);

        Assertions.assertEquals(new Date(2025 - 1900, 1, 10), mockCoachingProgram.getEndDate());
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }
    @Tag("unit")
    @Test
    public void testUpdateProgramEndDate_NullStepEndDate() {
        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setEndDate(new Date(2025 - 1900, 1, 10));

        Step step1 = new Step();
        step1.setStepEndDate(null);

        mockCoachingProgram.setTimeline(List.of(step1));

        Mockito.when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));

        coachingProgramService.updateProgramEndDate(coachingProgramId);

        Assertions.assertEquals(new Date(2025 - 1900, 1, 10), mockCoachingProgram.getEndDate());
        Mockito.verify(coachingProgramRepository, Mockito.never()).save(Mockito.any());
    }
    @Tag("unit")
    @Test
    public void testGetAllSessionsInCoachingProgram_Success() {
        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setGoal("Test Goal");

        Step step1 = new Step();
        step1.setStepId(1L);
        Session session1 = new Session();
        session1.setSessionId(101L);
        step1.setSessions(List.of(session1));

        Step step2 = new Step();
        step2.setStepId(2L);
        Session session2 = new Session();
        session2.setSessionId(102L);
        Session session3 = new Session();
        session3.setSessionId(103L);
        step2.setSessions(List.of(session2, session3));

        mockCoachingProgram.setTimeline(List.of(step1, step2));

        Mockito.when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));

        List<Session> result = coachingProgramService.getAllSessionsInCoachingProgram(coachingProgramId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.contains(session1));
        Assertions.assertTrue(result.contains(session2));
        Assertions.assertTrue(result.contains(session3));

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
    }

    @Tag("unit")
    @Test
    public void testGetAllSessionsInCoachingProgram_EmptyTimeline() {
        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setTimeline(List.of());

        Mockito.when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));

        List<Session> result = coachingProgramService.getAllSessionsInCoachingProgram(coachingProgramId);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
    }

    @Tag("unit")
    @Test
    public void testGetAllSessionsInCoachingProgram_NoSessionInStep() {
        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setGoal("Test Goal");

        Step step1 = new Step();
        step1.setStepId(1L);

        Step step2 = new Step();
        step2.setStepId(2L);

        step1.setSessions(List.of());
        step2.setSessions(List.of());

        mockCoachingProgram.setTimeline(List.of(step1, step2));

        Mockito.when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.of(mockCoachingProgram));

        List<Session> result = coachingProgramService.getAllSessionsInCoachingProgram(coachingProgramId);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
    }

    @Tag("unit")
    @Test
    public void testGetAllSessionsInCoachingProgram_CoachingProgramNotFound() {
        Long coachingProgramId = 6L;

        Mockito.when(coachingProgramRepository.findById(coachingProgramId)).thenReturn(Optional.empty());

        Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.getAllSessionsInCoachingProgram(coachingProgramId);
        });

        Mockito.verify(coachingProgramRepository, Mockito.times(1)).findById(coachingProgramId);
    }



}

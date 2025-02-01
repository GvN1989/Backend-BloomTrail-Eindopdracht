package nl.novi.bloomtrail.services;

import jakarta.validation.*;
import nl.novi.bloomtrail.dtos.CoachingProgramInputDto;
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
import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoachingProgramServiceUnitTest {

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
    private User mockClient;
    private User mockCoach;

    @BeforeEach
    public void setUp() {
        mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(1L);
        mockCoachingProgram.setCoachingProgramName("Original Program");
        mockCoachingProgram.setTimeline(new ArrayList<>());

        mockClient = new User();
        mockClient.setUsername("clientUser");

        mockCoach = new User();
        mockCoach.setUsername("coachUser");

        Mockito.lenient().when(coachingProgramRepository.findById(1L)).thenReturn(Optional.of(mockCoachingProgram));
        Mockito.lenient().when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        Mockito.lenient().when(validationHelper.validateUser("clientUser")).thenReturn(mockClient);
        Mockito.lenient().when(validationHelper.validateUser("coachUser")).thenReturn(mockCoach);

        Mockito.lenient().when(coachingProgramRepository.save(any(CoachingProgram.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

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
    void getCoachingProgramsByUser_Succes(){
        String username= "testUser";
        CoachingProgram program1= new CoachingProgram();
        program1.setCoachingProgramId(1L);
        program1.setCoachingProgramName("Program 1");

        CoachingProgram program2= new CoachingProgram();
        program2.setCoachingProgramId(2L);
        program2.setCoachingProgramName("Program 2");

        List<CoachingProgram> mockPrograms= List.of(program1, program2);

        when(coachingProgramRepository.findByUserUsername(username)).thenReturn(mockPrograms);

        List<CoachingProgram> result = coachingProgramService.getCoachingProgramsByUser(username);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Program 1", result.get(0).getCoachingProgramName());
        Assertions.assertEquals("Program 2", result.get(1).getCoachingProgramName());
    }
    @Tag("unit")
    @Test
    void getCoachingProgramsByUser_NoProgramsFound(){
        String username = "nonExistingUser";

        when(coachingProgramRepository.findByUserUsername(username)).thenReturn(Collections.emptyList());

        List<CoachingProgram> result = coachingProgramService.getCoachingProgramsByUser(username);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty(), "Expected an empty list when no programs are found.");
    }

    @Tag("unit")
    @Test
    void getCoachingProgramsByUser_NullUsername() {
        when(coachingProgramRepository.findByUserUsername(null)).thenReturn(Collections.emptyList());

        List<CoachingProgram> result = coachingProgramService.getCoachingProgramsByUser(null);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty(), "Expected an empty list when username is null.");
    }

    @Tag("unit")
    @Test
    void getStepsByCoachingProgram_Success() {
        Long coachingProgramId = 1L;

        CoachingProgram coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramId(coachingProgramId);

        Step step1 = new Step();
        step1.setStepId(1L);
        step1.setStepName("Step 1");

        Step step2 = new Step();
        step2.setStepId(2L);
        step2.setStepName("Step 2");

        List<Step> mockSteps = List.of(step1, step2);

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(coachingProgram);
        when(stepRepository.findStepsByCoachingProgram(coachingProgramId)).thenReturn(mockSteps);

        List<Step> result = coachingProgramService.getStepsByCoachingProgram(coachingProgramId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Step 1", result.get(0).getStepName());
        Assertions.assertEquals("Step 2", result.get(1).getStepName());
    }

    @Tag("unit")
    @Test
    void getStepsByCoachingProgram_NoStepsFound() {
        Long coachingProgramId = 2L;

        CoachingProgram coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramId(coachingProgramId);

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(coachingProgram);
        when(stepRepository.findStepsByCoachingProgram(coachingProgramId)).thenReturn(Collections.emptyList());

        Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.getStepsByCoachingProgram(coachingProgramId);
        }, "Expected RecordNotFoundException when no steps are found.");
    }

    @Tag("unit")
    @Test
    void getStepsByCoachingProgram_InvalidCoachingProgram() {
        Long invalidCoachingProgramId = 99L;

        when(validationHelper.validateCoachingProgram(invalidCoachingProgramId))
                .thenThrow(new IllegalArgumentException("Invalid coaching program"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.getStepsByCoachingProgram(invalidCoachingProgramId);
        }, "Expected IllegalArgumentException when coaching program is invalid.");
    }

    @Tag("unit")
    @Test
    void saveCoachingProgram_Success() {
        CoachingProgramInputDto inputDto = new CoachingProgramInputDto();
        inputDto.setClientUsername("clientUser");
        inputDto.setCoachUsername("coachUser");
        inputDto.setCoachingProgramName("Leadership Training");

        User client = new User();
        client.setUsername("clientUser");

        User coach = new User();
        coach.setUsername("coachUser");

        CoachingProgram coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramId(1L);
        coachingProgram.setCoachingProgramName("Leadership Training");
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);

        when(validationHelper.validateUser("clientUser")).thenReturn(client);
        when(validationHelper.validateUser("coachUser")).thenReturn(coach);
        when(coachingProgramRepository.save(any(CoachingProgram.class))).thenReturn(coachingProgram);

        CoachingProgram result = coachingProgramService.saveCoachingProgram(inputDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Leadership Training", result.getCoachingProgramName());
        Assertions.assertEquals("clientUser", result.getClient().getUsername());
        Assertions.assertEquals("coachUser", result.getCoach().getUsername());
    }

    @Tag("unit")
    @Test
    void saveCoachingProgram_InvalidClientUsername() {
        CoachingProgramInputDto inputDto = new CoachingProgramInputDto();
        inputDto.setClientUsername("invalidClient");
        inputDto.setCoachUsername("coachUser");

        when(validationHelper.validateUser("invalidClient"))
                .thenThrow(new IllegalArgumentException("Client user not found"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.saveCoachingProgram(inputDto);
        }, "Expected IllegalArgumentException when client username is invalid.");
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
    void saveCoachingProgram_InvalidCoachUsername() {
        CoachingProgramInputDto inputDto = new CoachingProgramInputDto();
        inputDto.setClientUsername("clientUser");
        inputDto.setCoachUsername("invalidCoach");

        User client = new User();
        client.setUsername("clientUser");

        when(validationHelper.validateUser("clientUser")).thenReturn(client);
        when(validationHelper.validateUser("invalidCoach"))
                .thenThrow(new IllegalArgumentException("Coach user not found"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.saveCoachingProgram(inputDto);
        }, "Expected IllegalArgumentException when coach username is invalid.");
    }

    @Test
    void saveCoachingProgram_DatabaseError() {
        CoachingProgramInputDto inputDto = new CoachingProgramInputDto();
        inputDto.setClientUsername("clientUser");
        inputDto.setCoachUsername("coachUser");

        User client = new User();
        client.setUsername("clientUser");

        User coach = new User();
        coach.setUsername("coachUser");

        when(validationHelper.validateUser("clientUser")).thenReturn(client);
        when(validationHelper.validateUser("coachUser")).thenReturn(coach);
        when(coachingProgramRepository.save(any(CoachingProgram.class)))
                .thenThrow(new RuntimeException("Database save error"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            coachingProgramService.saveCoachingProgram(inputDto);
        }, "Expected RuntimeException when database save fails.");
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

    @Tag("unit")
    @Test
    void updateCoachingProgram_Success() {

        CoachingProgramInputDto inputDto = new CoachingProgramInputDto();
        inputDto.setClientUsername("clientUser");
        inputDto.setCoachUsername("coachUser");
        inputDto.setCoachingProgramName("Updated Program");
        inputDto.setGoal("Updated Goal");
        inputDto.setStartDate(LocalDate.parse("2024-03-01"));
        inputDto.setEndDate(LocalDate.parse("2024-06-01"));

        CoachingProgram result = coachingProgramService.updateCoachingProgram(1L, inputDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated Program", result.getCoachingProgramName());
        Assertions.assertEquals("Updated Goal", result.getGoal());
        Assertions.assertEquals(LocalDate.parse("2024-03-01"), result.getStartDate());
        Assertions.assertEquals(LocalDate.parse("2024-06-01"), result.getEndDate());
        Assertions.assertEquals("clientUser", result.getClient().getUsername());
        Assertions.assertEquals("coachUser", result.getCoach().getUsername());
    }

    @Tag("unit")
    @Test
    void updateCoachingProgram_InvalidCoachingProgramId() {
        Long invalidCoachingProgramId = 99L;
        CoachingProgramInputDto inputDto = new CoachingProgramInputDto();

        when(validationHelper.validateCoachingProgram(invalidCoachingProgramId))
                .thenThrow(new RecordNotFoundException("Coaching program not found"));

        Assertions.assertThrows(RecordNotFoundException.class, () -> {
            coachingProgramService.updateCoachingProgram(invalidCoachingProgramId, inputDto);
        }, "Expected RecordNotFoundException when coaching program ID is invalid.");
    }

    @Tag("unit")
    @Test
    void updateCoachingProgram_InvalidClientUsername() {
        Long coachingProgramId = 1L;
        CoachingProgramInputDto inputDto = new CoachingProgramInputDto();
        inputDto.setClientUsername("invalidClient");
        inputDto.setCoachUsername("coachUser");

        CoachingProgram coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramId(coachingProgramId);

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(coachingProgram);
        when(validationHelper.validateUser("invalidClient"))
                .thenThrow(new IllegalArgumentException("Client user not found"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.updateCoachingProgram(coachingProgramId, inputDto);
        }, "Expected IllegalArgumentException when client username is invalid.");
    }

    @Tag("unit")
    @Test
    void updateCoachingProgram_NullStartDate() {
        Long coachingProgramId = 1L;
        CoachingProgramInputDto inputDto = new CoachingProgramInputDto();
        inputDto.setClientUsername("clientUser");
        inputDto.setCoachUsername("coachUser");
        inputDto.setCoachingProgramName("TestProgram");

        inputDto.setStartDate(null);
        inputDto.setEndDate(LocalDate.now().plusMonths(6));

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<CoachingProgramInputDto>> violations = validator.validate(inputDto);

        Assertions.assertFalse(violations.isEmpty(), "Expected validation violations for null startDate.");
    }

    @Tag("unit")
    @Test
    void updateCoachingProgram_DatabaseError() {
        Long coachingProgramId = 1L;

        CoachingProgramInputDto inputDto = new CoachingProgramInputDto();
        inputDto.setClientUsername("clientUser");
        inputDto.setCoachUsername("coachUser");
        inputDto.setCoachingProgramName("TestProgram");

        CoachingProgram coachingProgram = new CoachingProgram();
        coachingProgram.setCoachingProgramId(coachingProgramId);

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(coachingProgram);
        when(coachingProgramRepository.save(any(CoachingProgram.class)))
                .thenThrow(new RuntimeException("Database error"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            coachingProgramService.updateCoachingProgram(coachingProgramId, inputDto);
        }, "Expected RuntimeException when database save fails.");
    }



    @Test
    @Tag("unit")
    public void testAssignStepToCoachingProgram_SUCCES() {

        Long coachingProgramId = 1L;

        Step newStep = new Step();
        newStep.setSequence(1);
        newStep.setStepStartDate(LocalDate.parse("2025-01-10"));
        newStep.setStepEndDate(LocalDate.parse("2025-01-15"));

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

        Mockito.verify(validationHelper, Mockito.times(1)).validateCoachingProgram(coachingProgramId);
        Mockito.verify(stepRepository, Mockito.times(1)).findStepsByCoachingProgram(coachingProgramId);
        Mockito.verify(stepRepository, Mockito.times(1)).save(newStep);
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_DuplicateStep(){
        Step existingStep = new Step();
        existingStep.setStepId(1L);
        existingStep.setStepStartDate(LocalDate.parse("2025-01-10"));

        Step duplicateStep = new Step();
        duplicateStep.setStepId(1L);
        duplicateStep.setStepStartDate(LocalDate.parse("2025-01-15"));

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
        existingStep.setStepStartDate(LocalDate.parse("2025-01-10"));

        Step conflictingStep = new Step();
        conflictingStep.setSequence(1);
        conflictingStep.setStepStartDate(LocalDate.parse("2025-01-11"));

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
        existingStep.setStepStartDate(LocalDate.parse("2025-01-10"));

        Step conflictingStep = new Step();
        conflictingStep.setSequence(2);
        conflictingStep.setStepStartDate(LocalDate.parse("2025-01-01"));

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
    public void testAssignStepToCoachingProgram_StartDateSequenceConflict() {
        Step existingStep = new Step();
        existingStep.setSequence(1);
        existingStep.setStepStartDate(LocalDate.parse("2025-01-10"));

        Step conflictingStep = new Step();
        conflictingStep.setSequence(2);
        conflictingStep.setStepStartDate(LocalDate.parse("2025-01-05"));

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
        newStep.setStepStartDate(LocalDate.parse("2025-01-01"));

        when(validationHelper.validateCoachingProgram(invalidCoachingProgramId))
                .thenThrow(new EntityNotFoundException("CoachingProgram", invalidCoachingProgramId));

              EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
                  coachingProgramService.assignStepToCoachingProgram(invalidCoachingProgramId, newStep);
              });

        Assertions.assertEquals("CoachingProgram with ID 999 not found. ", exception.getMessage());

        Mockito.verify(stepRepository, Mockito.never()).save(Mockito.any(Step.class));
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_NoExistingSteps() {
        Step newStep = new Step();
        newStep.setSequence(1);
        newStep.setStepStartDate(LocalDate.parse("2025-01-10"));
        newStep.setStepEndDate(LocalDate.parse("2025-01-15"));

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(1L);
        mockCoachingProgram.setTimeline(new ArrayList<>()); // No existing steps

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.findStepsByCoachingProgram(1L)).thenReturn(new ArrayList<>());
        when(stepRepository.save(Mockito.any(Step.class))).thenReturn(newStep);

        CoachingProgram result = coachingProgramService.assignStepToCoachingProgram(1L, newStep);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTimeline().size(), "New step should be added");
        Assertions.assertEquals(newStep.getStepEndDate(), result.getTimeline().get(0).getStepEndDate(), "Step end date should be correctly assigned");
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_SameSequenceEarlierStartDate() {
        Step existingStep = new Step();
        existingStep.setSequence(2);
        existingStep.setStepStartDate(LocalDate.parse("2025-01-10"));

        Step conflictingStep = new Step();
        conflictingStep.setSequence(2);
        conflictingStep.setStepStartDate(LocalDate.parse("2025-01-05"));

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.findStepsByCoachingProgram(1L)).thenReturn(List.of(existingStep));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.assignStepToCoachingProgram(1L, conflictingStep);
        });

        Assertions.assertEquals("A step with sequence 2 already exists in this program.", exception.getMessage());

        Mockito.verify(stepRepository, Mockito.never()).save(Mockito.any(Step.class));
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_SameSequenceEqualStartDate() {
        Step existingStep = new Step();
        existingStep.setSequence(1);
        existingStep.setStepStartDate(LocalDate.parse("2025-01-10"));

        Step conflictingStep = new Step();
        conflictingStep.setSequence(1);
        conflictingStep.setStepStartDate(LocalDate.parse("2025-01-10"));

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.findStepsByCoachingProgram(1L)).thenReturn(List.of(existingStep));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.assignStepToCoachingProgram(1L, conflictingStep);
        });

        Assertions.assertEquals("A step with sequence 1 already exists in this program.", exception.getMessage());

        Mockito.verify(stepRepository, Mockito.never()).save(Mockito.any(Step.class));
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_SequenceStartDateConflict() {
        Step existingStep = new Step();
        existingStep.setSequence(1);
        existingStep.setStepStartDate(LocalDate.parse("2025-01-15"));
        Step newStep = new Step();
        newStep.setSequence(2);
        newStep.setStepStartDate(LocalDate.parse("2025-01-10"));

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.findStepsByCoachingProgram(1L)).thenReturn(List.of(existingStep));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.assignStepToCoachingProgram(1L, newStep);
        });

        Assertions.assertEquals("The step's start date conflicts with the provided sequence.", exception.getMessage());

        Mockito.verify(stepRepository, Mockito.never()).save(Mockito.any(Step.class));
    }

    @Tag("unit")
    @Test
    public void testAssignStepToCoachingProgram_ValidSequence_NoConflict() {
        Step existingStep = new Step();
        existingStep.setStepId(100L);
        existingStep.setSequence(1);
        existingStep.setStepStartDate(LocalDate.parse("2025-01-05"));
        existingStep.setStepEndDate(LocalDate.parse("2025-01-08"));

        Step newStep = new Step();
        newStep.setStepId(101L);
        newStep.setSequence(2);
        newStep.setStepStartDate(LocalDate.parse("2025-01-10"));
        newStep.setStepEndDate(LocalDate.parse("2025-01-15"));

        mockCoachingProgram.setTimeline(new ArrayList<>(List.of(existingStep)));

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);
        when(stepRepository.findStepsByCoachingProgram(1L)).thenReturn(List.of(existingStep));

        when(stepRepository.save(Mockito.any(Step.class))).thenAnswer(invocation -> {
            Step savedStep = invocation.getArgument(0);

            boolean isDuplicate = mockCoachingProgram.getTimeline().stream()
                    .anyMatch(s -> s.getSequence().equals(savedStep.getSequence())
                            && s.getStepStartDate().equals(savedStep.getStepStartDate()));

            if (!isDuplicate) {
                mockCoachingProgram.getTimeline().add(savedStep);
            }

            return savedStep;
        });

        CoachingProgram result = coachingProgramService.assignStepToCoachingProgram(1L, newStep);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getTimeline().size(), "The timeline should have 2 steps (1 existing + 1 new).");
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
    void testGetCoachingProgramWithSteps_Success() {
        CoachingProgram mockProgram = new CoachingProgram();
        mockProgram.setCoachingProgramId(1L);
        mockProgram.setTimeline(List.of(new Step(), new Step()));

        when(coachingProgramRepository.findByIdWithSteps(1L)).thenReturn(Optional.of(mockProgram));

        CoachingProgram result = coachingProgramService.getCoachingProgramWithSteps(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getCoachingProgramId());
        Assertions.assertEquals(2, result.getTimeline().size());
        verify(coachingProgramRepository, times(1)).findByIdWithSteps(1L);
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
        step1.setStepEndDate(LocalDate.parse("2025-01-10"));

        Step step2 = new Step();
        step2.setStepEndDate(LocalDate.parse("2025-01-15"));

        mockCoachingProgram.setTimeline(List.of(step1, step2));
        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(mockCoachingProgram);

        coachingProgramService.updateProgramEndDate(coachingProgramId);

        Assertions.assertEquals(
                LocalDate.from(LocalDate.parse("2025-01-15")),
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

        when(validationHelper.validateCoachingProgram(1L)).thenReturn(mockCoachingProgram);

        coachingProgramService.updateProgramEndDate(1L);


        Assertions.assertNull(mockCoachingProgram.getEndDate(), "End date should remain unchanged when timeline is empty");
        verify(coachingProgramRepository, never()).save(any(CoachingProgram.class));
    }


}

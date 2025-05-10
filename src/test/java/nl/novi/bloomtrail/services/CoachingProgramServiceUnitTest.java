package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.CoachingProgramDto;
import nl.novi.bloomtrail.dtos.CoachingProgramInputDto;
import nl.novi.bloomtrail.dtos.CoachingProgramUpdateDto;
import nl.novi.bloomtrail.dtos.SimpleCoachingProgramDto;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.AccessValidator;
import nl.novi.bloomtrail.helper.ValidationHelper;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoachingProgramServiceUnitTest {

    @Mock
    private CoachingProgramRepository coachingProgramRepository;

    @Mock
    private StepRepository stepRepository;

    @Mock
    private ValidationHelper validationHelper;

    @Mock
    private AccessValidator accessValidator;

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
        mockCoachingProgram.setGoal("Test Goal");

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

        Long coachingProgramId = 1L;

        CoachingProgram mockProgram = new CoachingProgram();
        mockProgram.setCoachingProgramId(coachingProgramId);
        mockProgram.setGoal("Test Goal");
        mockProgram.setTimeline(Collections.emptyList());

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(mockProgram);
        when(stepRepository.countByCoachingProgram_CoachingProgramId(coachingProgramId)).thenReturn(0);

        CoachingProgramDto result = coachingProgramService.findById(coachingProgramId);

        Assertions.assertNotNull(result, "The result should not be null");
        Assertions.assertEquals(coachingProgramId, result.getCoachingProgramId(), "The coaching program ID should match");
        Assertions.assertEquals("Test Goal", result.getGoal(), "The coaching program goal should match");

        Mockito.verify(validationHelper, Mockito.times(1)).validateCoachingProgram(coachingProgramId);
    }

    @Tag("unit")
    @Test
    public void testFindById_NotFound() {

        Long coachingProgramId = 6L;

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenThrow(new NotFoundException("Coaching Program not found with ID: " + coachingProgramId));

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
            coachingProgramService.findById(coachingProgramId);
        });

        Assertions.assertEquals("Coaching Program not found with ID: 6", exception.getMessage(), "Exception message should match");
    }

    @Tag("unit")
    @Test
    public void testGetCoachingProgramDetails_SUCCES() {
        User client = new User();
        client.setUsername("testClient");

        User coach = new User();
        coach.setUsername("testCoach");

        CoachingProgram program1 = new CoachingProgram();
        program1.setCoachingProgramId(1L);
        program1.setCoachingProgramName("Let's Go");
        program1.setClient(client);
        program1.setCoach(coach);
        program1.setTimeline(Collections.emptyList());

        CoachingProgram program2 = new CoachingProgram();
        program2.setCoachingProgramId(2L);
        program2.setCoachingProgramName("Keep Moving");
        program2.setClient(client);
        program2.setCoach(coach);
        program2.setTimeline(Collections.emptyList());

        when(coachingProgramRepository.findAll()).thenReturn(List.of(program1, program2));
        when(stepRepository.countByCoachingProgram_CoachingProgramId(1L)).thenReturn(5);
        when(stepRepository.countByCoachingProgram_CoachingProgramId(2L)).thenReturn(3);

        List<SimpleCoachingProgramDto> result = coachingProgramService.getCoachingProgramDetails();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Let's Go", result.get(0).getCoachingProgramName());
        Assertions.assertEquals("Keep Moving", result.get(1).getCoachingProgramName());

    }
    @Tag("unit")
    @Test
    public void testGetCoachingProgramDetails_NotFound_EmptyList() {

        when(coachingProgramRepository.findAll()).thenReturn(Collections.emptyList());

        List<SimpleCoachingProgramDto> result = coachingProgramService.getCoachingProgramDetails();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Tag("unit")
    @Test
    void testGetCoachingProgramSummariesForCoach_SUCCES() {
        String coachUsername = "coach1";

        User coach = new User();
        coach.setUsername(coachUsername);

        User client = new User();
        client.setUsername("client1");

        CoachingProgram mockProgram1 = new CoachingProgram();
        mockProgram1.setCoachingProgramId(1L);
        mockProgram1.setCoachingProgramName("Fit Life");
        mockProgram1.setCoach(coach);
        mockProgram1.setClient(client);
        mockProgram1.setTimeline(List.of(step(true), step(true), step(false), step(false)));

        CoachingProgram mockProgram2 = new CoachingProgram();
        mockProgram2.setCoachingProgramId(2L);
        mockProgram2.setCoachingProgramName("Strong Mind");
        mockProgram2.setCoach(coach);
        mockProgram2.setClient(client);
        mockProgram2.setTimeline(List.of(step(true), step(true), step(true)));

        when(coachingProgramRepository.findAllByCoach_Username(coachUsername))
                .thenReturn(List.of(mockProgram1, mockProgram2));

        when(stepRepository.countByCoachingProgram_CoachingProgramId(1L)).thenReturn(4);
        when(stepRepository.countByCoachingProgram_CoachingProgramId(2L)).thenReturn(3);

        List<SimpleCoachingProgramDto> result = coachingProgramService.getCoachingProgramSummariesForCoach(coachUsername);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals("Fit Life", result.get(0).getCoachingProgramName());
        Assertions.assertEquals(4, result.get(0).getStepCount());
        Assertions.assertEquals(50.0, result.get(0).getProgress(), 0.01);

        Assertions.assertEquals("Strong Mind", result.get(1).getCoachingProgramName());
        Assertions.assertEquals(3, result.get(1).getStepCount());
        Assertions.assertEquals(100.0, result.get(1).getProgress(), 0.01);
    }

    @Tag("unit")
    @Test
    void testGetCoachingProgramSummariesForCoach_EmptyList_ShouldReturnEmpty() {
        String coachUsername = "coachWithNoPrograms";

        when(coachingProgramRepository.findAllByCoach_Username(coachUsername))
                .thenReturn(Collections.emptyList());

        List<SimpleCoachingProgramDto> result = coachingProgramService.getCoachingProgramSummariesForCoach(coachUsername);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertTrue(result.isEmpty(), "Result should be an empty list");
    }

    @Tag("unit")
    @Test
    void getCoachingProgramsByUser_Succes(){
        String username= "testUser";
        CoachingProgram program1= new CoachingProgram();
        program1.setCoachingProgramId(1L);
        program1.setCoachingProgramName("Program 1");
        program1.setTimeline(Collections.emptyList());

        CoachingProgram program2= new CoachingProgram();
        program2.setCoachingProgramId(2L);
        program2.setCoachingProgramName("Program 2");
        program2.setTimeline(Collections.emptyList());

        List<CoachingProgram> mockPrograms= List.of(program1, program2);

        when(coachingProgramRepository.findByUserUsername(username)).thenReturn(mockPrograms);
        doNothing().when(accessValidator).validateAffiliatedUserOrAdmin(any(CoachingProgram.class));
        when(stepRepository.countByCoachingProgram_CoachingProgramId(anyLong())).thenReturn(0);

        List<CoachingProgramDto> result = coachingProgramService.getCoachingProgramsByUser(username);

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

        List<CoachingProgramDto> result = coachingProgramService.getCoachingProgramsByUser(username);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty(), "Expected an empty list when no programs are found.");
    }

    @Tag("unit")
    @Test
    void getCoachingProgramsByUser_NullUsername_ShouldThrowIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.getCoachingProgramsByUser(null);
        });
    }

    @Tag("unit")
    @Test
    void getCoachingProgramsByUser_ValidUsername_ShouldReturnList() {
        String username = "charlie";
        User mockUser = new User();
        mockUser.setUsername(username);

        CoachingProgram mockProgram = new CoachingProgram();
        mockProgram.setCoachingProgramId(1L);
        mockProgram.setCoachingProgramName("Program A");

        Step completedStep = new Step();
        completedStep.setCompleted(true);
        mockProgram.setTimeline(List.of(completedStep));

        List<CoachingProgram> programs = List.of(mockProgram);

        when(validationHelper.validateUser(username)).thenReturn(mockUser);
        doNothing().when(accessValidator).validateAffiliatedUserOrAdmin(any(CoachingProgram.class));
        when(coachingProgramRepository.findByUserUsername(username)).thenReturn(programs);
        when(stepRepository.countByCoachingProgram_CoachingProgramId(1L)).thenReturn(1);

        List<CoachingProgramDto> result = coachingProgramService.getCoachingProgramsByUser(username);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Program A", result.get(0).getCoachingProgramName());
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

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenThrow(new NotFoundException("CoachingProgram with ID " + coachingProgramId + " not found."));

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
            coachingProgramService.deleteCoachingProgram(coachingProgramId);
        });

        Assertions.assertEquals("CoachingProgram with ID 6 not found.", exception.getMessage());

        Mockito.verify(coachingProgramRepository, Mockito.never()).deleteById(Mockito.any());
    }

    @Tag("unit")
    @Test
    void updateCoachingProgram_Success() {

        CoachingProgramUpdateDto inputDto = new CoachingProgramUpdateDto();
        inputDto.setCoachUsername("coachUser");
        inputDto.setCoachingProgramName("Updated Program");
        inputDto.setGoal("Updated Goal");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        inputDto.setStartDate(LocalDate.parse("01-03-2024", formatter));
        inputDto.setEndDate(LocalDate.parse("01-06-2024", formatter));

        String clientUsername = "clientUser";
        Long programId = 1L;

        User client = new User();
        client.setUsername("clientUser");

        User coach = new User();
        coach.setUsername("coachUser");

        CoachingProgram program = new CoachingProgram();
        program.setClient(client);

        Mockito.when(validationHelper.validateUser("coachUser")).thenReturn(coach);
        Mockito.when(validationHelper.validateCoachingProgram(programId)).thenReturn(program);
        Mockito.when(coachingProgramRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(accessValidator).validateCoachOwnsProgramOrIsAdmin(program);

        CoachingProgram result = coachingProgramService.updateCoachingProgram("clientUser",1L, inputDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated Program", result.getCoachingProgramName());
        Assertions.assertEquals("Updated Goal", result.getGoal());
        Assertions.assertEquals(LocalDate.parse("01-03-2024", formatter), result.getStartDate());
        Assertions.assertEquals(LocalDate.parse("01-06-2024", formatter), result.getEndDate());
        Assertions.assertEquals("clientUser", result.getClient().getUsername());
        Assertions.assertEquals("coachUser", result.getCoach().getUsername());
    }

    @Tag("unit")
    @Test
    void updateCoachingProgram_InvalidCoachingProgramId() {
        Long invalidCoachingProgramId = 99L;
        String username = "clientUser";

        CoachingProgramUpdateDto inputDto = new CoachingProgramUpdateDto();

        when(validationHelper.validateCoachingProgram(invalidCoachingProgramId))
                .thenThrow(new NotFoundException("Coaching program not found"));

        Assertions.assertThrows(NotFoundException.class, () -> {
            coachingProgramService.updateCoachingProgram(username, invalidCoachingProgramId, inputDto);
        });
    }

    @Tag("unit")
    @Test
    void updateCoachingProgram_InvalidClientUsername() {
        Long coachingProgramId = 1L;
        String invalidClientUsername = "invalidClient";

        CoachingProgramUpdateDto inputDto = new CoachingProgramUpdateDto();
        inputDto.setCoachUsername(invalidClientUsername);

        when(validationHelper.validateUser(invalidClientUsername))
                .thenThrow(new IllegalArgumentException("Client user not found"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            coachingProgramService.updateCoachingProgram(invalidClientUsername, coachingProgramId, inputDto);
        });
    }

    @Tag("unit")
    @Test
    void updateCoachingProgram_DatabaseError() {
        Long coachingProgramId = 1L;
        String username = "clientUser";

        CoachingProgramUpdateDto inputDto = new CoachingProgramUpdateDto();
        inputDto.setGoal("Test goal");

        User client = new User();
        client.setUsername(username);

        CoachingProgram program = new CoachingProgram();
        program.setClient(client);

        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(program);
        when(coachingProgramRepository.save(any(CoachingProgram.class)))
                .thenThrow(new RuntimeException("Database error"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            coachingProgramService.updateCoachingProgram(username, coachingProgramId, inputDto);
        });
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

        double progressPercentage = coachingProgramService.calculateProgressPercentage(mockCoachingProgram);

        Assertions.assertEquals(66.67, progressPercentage, 0.01);
    }

    @Tag ("unit")
    @Test
    public void testCalculateProgressPercentage_EmptyTimeline() {

        Long coachingProgramId = 6L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);
        mockCoachingProgram.setTimeline(Collections.emptyList());

        double progressPercentage = coachingProgramService.calculateProgressPercentage(mockCoachingProgram);

        Assertions.assertEquals(0.0, progressPercentage);
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

        double progressPercentage = coachingProgramService.calculateProgressPercentage(mockCoachingProgram);

        Assertions.assertEquals(100.0, progressPercentage);
    }

    @Tag("unit")
    @Test
    public void testCalculateProgressPercentage_WhenStepMarkedComplete() {
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

        double initialProgressPercentage = coachingProgramService.calculateProgressPercentage(mockCoachingProgram);

        Assertions.assertEquals(66.67, initialProgressPercentage, 0.01);

        step3.setCompleted(true);

        double updatedProgressPercentage = coachingProgramService.calculateProgressPercentage(mockCoachingProgram);

        Assertions.assertEquals(100.0, updatedProgressPercentage, 0.01);
    }

    @Test
    @Tag("unit")
    void testToDtoWithMetrics_ShouldReturnValidDto() {
        CoachingProgram program = new CoachingProgram();
        program.setCoachingProgramId(1L);
        program.setTimeline(Collections.emptyList());

        when(stepRepository.countByCoachingProgram_CoachingProgramId(1L)).thenReturn(3);

        CoachingProgramDto result = coachingProgramService.toDtoWithMetrics(program);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.getStepCount());
    }

    @Test
    @Tag("unit")
    void testToSimpleDtoWithMetrics_ShouldReturnValidDto() {
        CoachingProgram simpleMockProgram = new CoachingProgram();
        simpleMockProgram.setCoachingProgramId(1L);
        simpleMockProgram.setTimeline(Collections.emptyList());

        when(stepRepository.countByCoachingProgram_CoachingProgramId(1L)).thenReturn(3);

        SimpleCoachingProgramDto result = coachingProgramService.toSimpleDtoWithMetrics(simpleMockProgram);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.getStepCount());
    }


    @Tag("unit")
    @Test
    public void testUpdateProgramEndDate_WithSteps() {
        Long coachingProgramId = 1L;

        CoachingProgram mockCoachingProgram = new CoachingProgram();
        mockCoachingProgram.setCoachingProgramId(coachingProgramId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Step step1 = new Step();
        step1.setStepEndDate(LocalDate.parse("10-01-2025", formatter));

        Step step2 = new Step();
        step2.setStepEndDate(LocalDate.parse("15-01-2025", formatter));

        mockCoachingProgram.setTimeline(List.of(step1, step2));
        when(validationHelper.validateCoachingProgram(coachingProgramId)).thenReturn(mockCoachingProgram);

        coachingProgramService.updateProgramEndDate(coachingProgramId);

        Assertions.assertEquals(
                (LocalDate.parse("15-01-2025", formatter)),
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

    private Step step(boolean completed) {
        Step step = new Step();
        step.setCompleted(completed);
        return step;
    }

}

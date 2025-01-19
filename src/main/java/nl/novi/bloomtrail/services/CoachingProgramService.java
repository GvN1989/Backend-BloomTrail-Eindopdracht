package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.CoachingProgramInputDto;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class CoachingProgramService {

    private final CoachingProgramRepository coachingProgramRepository;

    private final StrengthResultsRepository strengthResultsRepository;

    private final UserRepository userRepository;

    private final StepRepository stepRepository;


    public CoachingProgramService(CoachingProgramRepository coachingProgramRepository, StrengthResultsRepository strengthResultsRepository, UserRepository userRepository, StepRepository stepRepository) {
        this.coachingProgramRepository = coachingProgramRepository;
        this.strengthResultsRepository = strengthResultsRepository;
        this.stepRepository = stepRepository;
        this.userRepository = userRepository;
    }

    public List<CoachingProgram> findByUser(String username) {
        List<CoachingProgram> programs = coachingProgramRepository.findByUsername(username);
        if (programs.isEmpty()) {
            throw new RecordNotFoundException("No CoachingPrograms found for user with username: " + username);
        }
        return programs;
    }

    public CoachingProgram findById(Long coachingProgramId){
        return coachingProgramRepository.findById(coachingProgramId)
                .orElseThrow(()-> new RecordNotFoundException("CoachingProgram with id: " + coachingProgramId + "not found"));
    }

       public CoachingProgram saveCoachingProgram (CoachingProgramInputDto inputDto) {
        CoachingProgram coachingProgram= CoachingProgramMapper.toCoachingProgramEntity(inputDto);
        return coachingProgramRepository.save(coachingProgram);
    }

    public CoachingProgram updateCoachingProgram (Long coachingProgramId, CoachingProgramInputDto inputDto) {

        if (!coachingProgramRepository.existsById(coachingProgramId)) {
            throw new RecordNotFoundException("No coachingProgram found with ID " + coachingProgramId);
        }

        CoachingProgram coachingProgram = CoachingProgramMapper.toCoachingProgramEntity(inputDto);
        coachingProgram.setCoachingProgramId(coachingProgramId);
        return coachingProgramRepository.save(coachingProgram);
    }

    public void deleteCoachingProgram (Long coachingProgramId) {
        if (!coachingProgramRepository.existsById(coachingProgramId)) {
            throw new RecordNotFoundException("No coaching program found with ID " + coachingProgramId);
        }

        coachingProgramRepository.deleteById(coachingProgramId); }

    public CoachingProgram assignUserToCoachingProgram (Long coachingId, String username) {

        CoachingProgram coachingProgram = findById(coachingId);

        User user = userRepository.findById(username)
                .orElseThrow(() -> new RecordNotFoundException( "User with " + username + " not found"));

        coachingProgram.setUser(user);

        return coachingProgramRepository.save(coachingProgram);
    }

    public CoachingProgram assignStepToCoachingProgram (Long coachingId, Long stepId) {

        CoachingProgram coachingProgram = findById(coachingId);

        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RecordNotFoundException( "Step with " + stepId  + " not found"));

        List<Step> timeline = coachingProgram.getTimeline();

        if (timeline.contains(step)) {
            throw new IllegalArgumentException("Step is already part of the timeline.");
        }

        boolean stepNameExists = timeline.stream()
                .anyMatch(existingStep -> existingStep.getStepName().equalsIgnoreCase(step.getStepName()));
        if (stepNameExists) {
            throw new IllegalArgumentException("A step with the name '" + step.getStepName() + "' already exists in the timeline.");
        }
        timeline.add(step);
        coachingProgram.setTimeline(timeline);
        return coachingProgramRepository.save(coachingProgram);
    }

    public double calculateProgressPercentage(Long coachingId) {
        CoachingProgram coachingProgram = findById(coachingId);

        List<Step> timeline = coachingProgram.getTimeline();

        if (timeline.isEmpty()) {
            return 0.0;
        }

        long completedSteps = timeline.stream()
                .filter(Step::getCompleted)
                .count();

        return (double) completedSteps / timeline.size() * 100;
    }

    public CoachingProgram assignCoachingResultsToCoachingProgram(Long coachingId, Long strengthResultsId) {
        CoachingProgram coachingProgram = findById(coachingId);
        StrengthResults strengthResults = strengthResultsRepository.findById(strengthResultsId)
                .orElseThrow(() -> new RecordNotFoundException("CoachingResults with ID " + strengthResultsId + " not found"));

        coachingProgram.setStrengthResults(strengthResults);

        return coachingProgramRepository.save(coachingProgram);
    }

    public void updateProgramEndDate(Long coachingId) {
        CoachingProgram coachingProgram = findById(coachingId);

        List<Step> steps = coachingProgram.getTimeline();
        if (!steps.isEmpty()) {
            Date latestEndDate = steps.stream()
                    .map(Step::getStepEndDate)
                    .filter(Objects::nonNull)
                    .max(Date::compareTo)
                    .orElse(coachingProgram.getEndDate());
            if (latestEndDate.after(coachingProgram.getEndDate())) {
                coachingProgram.setEndDate(latestEndDate);
                coachingProgramRepository.save(coachingProgram);
            }
        }
    }
    public List<Session> getAllSessionsInCoachingProgram(Long coachingId) {
        CoachingProgram coachingProgram = findById(coachingId);

        return coachingProgram.getTimeline().stream()
                .flatMap(step -> step.getSessions().stream())
                .collect(Collectors.toList());
    }

}

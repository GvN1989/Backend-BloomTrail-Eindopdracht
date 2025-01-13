package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StrengthProgramInputDto;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StrengthProgramService {

    private final StrengthProgramRepository strengthProgramRepository;

    private final StrengthResultsRepository strengthResultsRepository;

    private final UserRepository userRepository;

    private final StepRepository stepRepository;


    public StrengthProgramService(StrengthProgramRepository strengthProgramRepository, SessionRepository sessionRepository, StrengthResultsRepository strengthResultsRepository, UserRepository userRepository, StepRepository stepRepository) {
        this.strengthProgramRepository = strengthProgramRepository;
        this.strengthResultsRepository = strengthResultsRepository;
        this.stepRepository = stepRepository;
        this.userRepository = userRepository;
    }

    public List<StrengthProgram> findByUser(String username) {
        return strengthProgramRepository.findByUserUsername(username);
    }

    public StrengthProgram findById(Long strengthProgramId){
        return strengthProgramRepository.findById(strengthProgramId)
                .orElseThrow(()-> new RecordNotFoundException("StrengthProgram with id: " + strengthProgramId + "not found"));
    }

    public StrengthProgram saveStrengthProgram (StrengthProgramInputDto inputDto) {
        StrengthProgram strengthProgram= StrengthProgramMapper.toStrengthProgramEntity(inputDto);
        return strengthProgramRepository.save(strengthProgram);
    }

    public StrengthProgram updateStrengthProgram (Long strengthProgramId, StrengthProgramInputDto inputDto) {

        if (!strengthProgramRepository.existsById(strengthProgramId)) {
            throw new RecordNotFoundException("No strengthProgram found with ID " + strengthProgramId);
        }

        StrengthProgram strengthProgram = StrengthProgramMapper.toStrengthProgramEntity(inputDto);
        strengthProgram.setStrengthProgramId(strengthProgramId);
        return strengthProgramRepository.save(strengthProgram);
    }

    public void deleteStrengthProgram (Long strengthProgramId) {
        if (!strengthProgramRepository.existsById(strengthProgramId)) {
            throw new RecordNotFoundException("No television found with ID " + strengthProgramId);
        }

        strengthProgramRepository.deleteById(strengthProgramId); }

    public StrengthProgram assignUserToStrengthProgram ( Long strengthProgramId, String username) {

        StrengthProgram strengthProgram = strengthProgramRepository.findById(strengthProgramId)
                .orElseThrow(()-> new RecordNotFoundException("StrengthProgram with ID " + strengthProgramId + " not found"));

        User user = userRepository.findById(username)
                .orElseThrow(() -> new RecordNotFoundException( "User with " + username + " not found"));

        strengthProgram.setUser(user);

        return strengthProgramRepository.save(strengthProgram);
    }

    public StrengthProgram assignStepToStrengthProgram ( Long strengthProgramId, Long stepId) {

        StrengthProgram strengthProgram = strengthProgramRepository.findById(strengthProgramId)
                .orElseThrow(()-> new RecordNotFoundException("StrengthProgram with ID " + strengthProgramId + " not found"));

        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RecordNotFoundException( "Step with " + stepId  + " not found"));

        List<Step> timeline = strengthProgram.getTimeline();
        if (!timeline.contains(step)) {
            timeline.add(step);
        } else {
            throw new IllegalArgumentException("Step is already part of the timeline.");
        }
        strengthProgram.setTimeline(timeline);

        return strengthProgramRepository.save(strengthProgram);
    }

    public double calculateProgressPercentage(Long strengthProgramId) {
        StrengthProgram strengthProgram = strengthProgramRepository.findById(strengthProgramId)
                .orElseThrow(() -> new RecordNotFoundException("StrengthProgram with ID " + strengthProgramId + " not found"));

        List<Step> timeline = strengthProgram.getTimeline();

        if (timeline.isEmpty()) {
            return 0.0;
        }

        long completedSteps = timeline.stream()
                .filter(Step::getCompleted)
                .count();

        return (double) completedSteps / timeline.size() * 100;
    }

    public StrengthProgram assignStrengthResultsToStrengthProgram(Long strengthProgramId, Long strengthResultsId) {
        StrengthProgram strengthProgram = findById(strengthProgramId);
        StrengthResults strengthResults = strengthResultsRepository.findById(strengthResultsId)
                .orElseThrow(() -> new RecordNotFoundException("StrengthResults with ID " + strengthResultsId + " not found"));

        strengthProgram.setStrengthResults(strengthResults);

        return strengthProgramRepository.save(strengthProgram);
    }

    public void updateProgramEndDate(Long strengthProgramId) {
        StrengthProgram strengthProgram = strengthProgramRepository.findById(strengthProgramId)
                .orElseThrow(() -> new RecordNotFoundException("StrengthProgram with ID " + strengthProgramId + " not found"));

        List<Step> steps = strengthProgram.getTimeline();
        if (!steps.isEmpty()) {
            Date latestEndDate = steps.stream()
                    .map(Step::getStepEndDate)
                    .max(Date::compareTo)
                    .orElse(strengthProgram.getEndDate());

            if (latestEndDate.after(strengthProgram.getEndDate())) {
                strengthProgram.setEndDate(latestEndDate);
                strengthProgramRepository.save(strengthProgram);
            }
        }

    }

    public List<Session> getAllSessionsInStrengthProgram(Long strengthProgramId) {
        StrengthProgram strengthProgram = strengthProgramRepository.findById(strengthProgramId)
                .orElseThrow(() -> new RecordNotFoundException("StrengthProgram with ID " + strengthProgramId + " not found"));

        return strengthProgram.getTimeline().stream()
                .flatMap(step -> step.getSessions().stream())
                .collect(Collectors.toList());
    }



}

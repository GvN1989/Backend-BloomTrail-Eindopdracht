package nl.novi.bloomtrail.helper;

import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.repositories.StepRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class StepSequenceHelper {

    private final StepRepository stepRepository;

    public StepSequenceHelper(StepRepository stepRepository) {
        this.stepRepository = stepRepository;
    }

    public void reorderStepsForProgram(CoachingProgram program) {
        List<Step> steps = stepRepository.findByCoachingProgram(program).stream()
                .sorted(Comparator.comparing(Step::getStepStartDate).thenComparing(Step::getStepId))
                .toList();

        int seq = 1;
        for (Step s : steps) {
            s.setSequence(seq++);
        }

        stepRepository.saveAll(steps);
    }

}

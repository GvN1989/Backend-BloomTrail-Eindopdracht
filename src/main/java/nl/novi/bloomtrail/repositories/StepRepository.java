package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {

    List<Step> findByCoachingProgram(CoachingProgram coachingProgram);

    int countByCoachingProgram_CoachingProgramId(Long programId);


}

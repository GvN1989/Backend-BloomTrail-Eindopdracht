package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StepRepository extends JpaRepository<Step, Long> {

    List<Step> findByCoachingProgram(CoachingProgram coachingProgram);

    @EntityGraph(attributePaths = {"assignments"})
    Optional<Step> findByStepId(Long stepId);

}

package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {

    @Query("SELECT s FROM Step s WHERE s.coachingProgram.coachingProgramId = :coachingProgramId")
    List<Step> findStepsByCoachingProgram(@Param("coachingProgramId") Long coachingProgramId);

}

package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.StrengthResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface StrengthResultsRepository extends JpaRepository <StrengthResults, Long> {
    List<StrengthResults> findByCoachingProgram(CoachingProgram coachingProgram);

    List<StrengthResults> findAllById(@NonNull Iterable<Long> ids);

}

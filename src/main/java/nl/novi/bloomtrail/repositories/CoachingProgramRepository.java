package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.dtos.SimpleCoachingProgramDto;
import nl.novi.bloomtrail.models.CoachingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CoachingProgramRepository extends JpaRepository<CoachingProgram, Long> {
    @Query("SELECT cp FROM CoachingProgram cp WHERE cp.client.username = :username OR cp.coach.username = :username")
    List<CoachingProgram> findByUserUsername(@Param("username") String username);

    List<CoachingProgram> findByCoachingProgramNameIgnoreCase(String coachingProgramName);

    Optional<CoachingProgram> findById(Long id);

    @Query("SELECT new nl.novi.bloomtrail.dtos.SimpleCoachingProgramDto(" +
            "c.coachingProgramId, c.coachingProgramName, c.client.username, c.coach.username) " +
            "FROM CoachingProgram c")
    List<SimpleCoachingProgramDto> findAllCoachingProgramDetails();

    @Query("SELECT c FROM CoachingProgram c LEFT JOIN FETCH c.timeline WHERE c.coachingProgramId = :id")
    Optional<CoachingProgram> findByIdWithSteps(@Param("id") Long id);
}

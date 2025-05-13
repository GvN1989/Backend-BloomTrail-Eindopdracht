package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.CoachingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CoachingProgramRepository extends JpaRepository<CoachingProgram, Long> {
    @Query("SELECT cp FROM CoachingProgram cp WHERE cp.client.username = :username OR cp.coach.username = :username")
    List<CoachingProgram> findByUserUsername(@Param("username") String username);

    List<CoachingProgram> findAllByCoach_Username(String username);

    List<CoachingProgram> findByCoachingProgramNameIgnoreCase(String coachingProgramName);

    Optional<CoachingProgram> findByCoachingProgramId(Long coachingProgramId);

    @Query("SELECT COUNT(cp) > 0 FROM CoachingProgram cp " +
            "WHERE cp.coach.username = :coachUsername AND cp.client.username = :clientUsername")
    boolean existsByCoachUsernameAndClientUsername(
            @Param("coachUsername") String coachUsername,
            @Param("clientUsername") String clientUsername);


}

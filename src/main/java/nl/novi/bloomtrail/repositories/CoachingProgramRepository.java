package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.StrengthResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoachingProgramRepository extends JpaRepository<CoachingProgram, Long> {

    @Query("SELECT cp FROM CoachingProgram cp WHERE cp.user.username = :username")
    List<CoachingProgram> findByUsername(@Param("username") String username);

    List<CoachingProgram> findByCoachingProgramNameAndUsername(String coachingProgramName, String username);

    List<CoachingProgram> findByCoachingProgramName(String coachingProgramName);





}

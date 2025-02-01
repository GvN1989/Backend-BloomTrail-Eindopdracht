package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.dtos.SimpleCoachingProgramDto;
import nl.novi.bloomtrail.models.CoachingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CoachingProgramRepository extends JpaRepository<CoachingProgram, Long> {
    @Query("SELECT cp FROM CoachingProgram cp WHERE cp.client.username = :username OR cp.coach.username = :username")
    List<CoachingProgram> findByUserUsername(@Param("username") String username);

    @Query("SELECT new nl.novi.bloomtrail.dtos.SimpleCoachingProgramDto(" +
            "c.coachingProgramId, c.coachingProgramName, c.client.username, c.coach.username) " +
            "FROM CoachingProgram c")
    List<SimpleCoachingProgramDto> findAllCoachingProgramDetails();
}

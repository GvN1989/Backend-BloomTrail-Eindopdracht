package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String > {

    @Query("SELECT cp FROM CoachingProgram cp WHERE cp.coach.username = :username")
    List<CoachingProgram> findCoachingProgramsAsCoach(@Param("username") String username);

    @Query("SELECT cp FROM CoachingProgram cp WHERE cp.client.username = :username")
    List<CoachingProgram> findCoachingProgramsAsClient(@Param("username") String username);
}


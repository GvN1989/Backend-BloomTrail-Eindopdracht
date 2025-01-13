package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.StrengthProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StrengthProgramRepository extends JpaRepository<StrengthProgram, Long> {

    @Query("SELECT sp FROM StrengthProgram sp WHERE sp.user.username = :username")
    List<StrengthProgram> findByUserUsername(@Param("username") String username);


}

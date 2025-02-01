package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.ManagingStrength;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagingStrengthRepository extends JpaRepository<ManagingStrength, Long> {

    @Query("SELECT ms FROM ManagingStrength ms " +
            "JOIN ms.strengthResults sr " +
            "JOIN sr.coachingProgram cp " +
            "JOIN cp.client u " +
            "WHERE u.username = :username " +
            "ORDER BY ms.rank ASC")
    List<ManagingStrength> findTop15ByUserUsernameOrderByRank(@Param("username") String username);

}


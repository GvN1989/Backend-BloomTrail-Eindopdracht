package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionRepository extends JpaRepository <Session, Long> {

    @Query("SELECT a.assignmentId FROM Assignment a WHERE a.session = :session")
    List<Long> findIdsBySession(@Param("session") Session session);

    @Query("SELECT s.sessionInsightId FROM SessionInsight s WHERE s.session = :session")
    List<Long> findInsightIdsBySession(@Param("session") Session session);

}

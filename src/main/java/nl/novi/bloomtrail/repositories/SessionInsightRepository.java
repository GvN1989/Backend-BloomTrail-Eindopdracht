package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.SessionInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface SessionInsightRepository extends JpaRepository<SessionInsight, Long> {

    @EntityGraph(attributePaths = {"files"})
    Optional<SessionInsight> findBySession(Session session);



}

package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.SessionInsight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionInsightsRepository extends JpaRepository<SessionInsight, Long>
{
}

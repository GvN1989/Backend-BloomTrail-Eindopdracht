package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.SessionInsights;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionInsightsRepository extends JpaRepository<SessionInsights, Long>
{
}

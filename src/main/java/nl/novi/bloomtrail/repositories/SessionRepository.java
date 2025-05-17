package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository <Session, Long> {


}

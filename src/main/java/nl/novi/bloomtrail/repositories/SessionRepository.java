package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionRepository extends JpaRepository <Session, Long> {


}

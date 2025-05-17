package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.StrengthResults;
import nl.novi.bloomtrail.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StrengthResultsRepository extends JpaRepository <StrengthResults, Long> {
    Optional<StrengthResults> findByUser(User user);

}

package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.ManagingStrength;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagingStrengthsRepository extends JpaRepository<ManagingStrength, Integer>, CustomManagingStrengthRepository {
}


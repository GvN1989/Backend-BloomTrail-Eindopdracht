package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.ManagingStrength;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagingStrengthRepository extends JpaRepository<ManagingStrength, Long> {

    List<ManagingStrength> findTop15ByUsernameOrderByRank(String username);

}


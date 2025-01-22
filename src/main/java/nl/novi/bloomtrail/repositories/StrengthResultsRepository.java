package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.ManagingStrength;
import nl.novi.bloomtrail.models.StrengthResults;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StrengthResultsRepository extends JpaRepository <StrengthResults, Long> {



}

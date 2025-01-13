package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.ManagingStrength;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ManagingStrengthRepository extends JpaRepository <ManagingStrength, Long>{

    List<ManagingStrength> findByStrengthEnContainingIgnoreCaseOrStrengthNlContainingIgnoreCase(String strengthEn, String strengthNl);

}

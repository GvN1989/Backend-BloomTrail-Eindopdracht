package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.ManagingStrength;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagingStrengthsRepository extends JpaRepository<ManagingStrength, Integer> {

    @Query("SELECT m FROM ManagingStrength m WHERE m.strengthId IN :strengthIds")
    List<ManagingStrength> findByIdIn(@Param("strengthIds") List<Long> strengthIds);
    List<ManagingStrength> findTop15ByUserIdOrderByRank(Long userId);

}


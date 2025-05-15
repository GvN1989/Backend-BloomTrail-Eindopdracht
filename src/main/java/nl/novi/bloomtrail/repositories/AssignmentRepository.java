package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.Assignment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @EntityGraph(attributePaths = {"files"})
    Optional<Assignment> findByAssignmentId(Long id);

}

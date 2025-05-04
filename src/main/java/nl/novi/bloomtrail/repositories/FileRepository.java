package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByContext(FileContext context);

    List<File> findByAssignment(Assignment assignment);

    List<File> findBySessionInsight(SessionInsight sessionInsight);
}

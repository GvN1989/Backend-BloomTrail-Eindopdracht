package nl.novi.bloomtrail.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import nl.novi.bloomtrail.models.Authority;
import nl.novi.bloomtrail.models.AuthorityKey;

public interface AuthorityRepository extends JpaRepository<Authority, AuthorityKey> {
}


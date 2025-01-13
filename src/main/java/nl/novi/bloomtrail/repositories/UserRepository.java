package nl.novi.bloomtrail.repositories;

import nl.novi.bloomtrail.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String > {
}

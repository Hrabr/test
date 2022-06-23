package ua.goit.group2notes.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserDao, UUID> {

    Optional<UserDao> findUserByUsername(String username);

}

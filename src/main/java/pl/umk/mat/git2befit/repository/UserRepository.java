package pl.umk.mat.git2befit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

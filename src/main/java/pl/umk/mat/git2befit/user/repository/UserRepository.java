package pl.umk.mat.git2befit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.user.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

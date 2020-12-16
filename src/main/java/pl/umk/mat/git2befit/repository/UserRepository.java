package pl.umk.mat.git2befit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}

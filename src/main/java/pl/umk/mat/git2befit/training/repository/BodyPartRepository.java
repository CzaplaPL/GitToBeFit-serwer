package pl.umk.mat.git2befit.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.training.model.conditions.BodyPart;

public interface BodyPartRepository extends JpaRepository<BodyPart, Long> {
}

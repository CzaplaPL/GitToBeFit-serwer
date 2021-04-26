package pl.umk.mat.git2befit.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.training.model.conditions.TrainingType;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
}

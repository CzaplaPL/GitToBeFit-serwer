package pl.umk.mat.git2befit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.model.entity.workout.conditions.TrainingType;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
}

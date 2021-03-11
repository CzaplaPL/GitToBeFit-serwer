package pl.umk.mat.git2befit.repository.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.model.workout.conditions.TrainingType;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
}

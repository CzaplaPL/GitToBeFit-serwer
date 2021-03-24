package pl.umk.mat.git2befit.repository.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;

import java.util.List;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    List<TrainingPlan> findAllByUserIdOrderByIdDesc(Long userId);
}

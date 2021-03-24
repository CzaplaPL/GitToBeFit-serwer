package pl.umk.mat.git2befit.repository.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;

import java.util.List;
import java.util.Optional;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    List<TrainingPlan> findAllByUserIdOrderByIdDesc(Long userId);
    Optional<TrainingPlan> findByIdAndUserId(long id, Long user_id);
}

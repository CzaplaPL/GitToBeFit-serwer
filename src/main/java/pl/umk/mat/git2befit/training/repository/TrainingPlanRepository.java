package pl.umk.mat.git2befit.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.training.model.training.TrainingPlan;

import java.util.List;
import java.util.Optional;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    List<TrainingPlan> findAllByUser_Email(String email);
    Optional<TrainingPlan> findByIdAndUserId(long id, Long user_id);
}

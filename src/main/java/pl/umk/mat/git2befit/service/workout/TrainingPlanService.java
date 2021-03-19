package pl.umk.mat.git2befit.service.workout;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;
import pl.umk.mat.git2befit.repository.workout.TrainingPlanRepository;

@Service
public class TrainingPlanService {
    private final TrainingPlanRepository repository;

    public TrainingPlanService(TrainingPlanRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity<?> save(TrainingPlan trainingPlan) {
        try {
            this.repository.save(trainingPlan);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().header("Cause", "cant be saved").build();
        }
    }
}

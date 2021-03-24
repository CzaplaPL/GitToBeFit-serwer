package pl.umk.mat.git2befit.service.workout;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.user.entity.User;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;
import pl.umk.mat.git2befit.repository.user.UserRepository;
import pl.umk.mat.git2befit.repository.workout.TrainingPlanRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingPlanService {
    private final TrainingPlanRepository trainingPlanRepository;
    private final UserRepository userRepository;

    public TrainingPlanService(TrainingPlanRepository trainingPlanRepository, UserRepository userRepository) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> save(List<TrainingPlan> trainingPlans, long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                trainingPlans.forEach(plan -> {
                    plan.setUser(user.get());
                    trainingPlanRepository.save(plan);
                });
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().header("Cause", "user not found").build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().header("Cause", "cant be saved").build();
        }
    }

    public List<TrainingPlan> getAllTrainingPlansByUserId(long userId) {
        return trainingPlanRepository.findAllByUserIdOrderByIdDesc(userId);
    }

    public ResponseEntity<?> getTrainingPlanByIdForUser(long trainingPlanId, long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                Optional<TrainingPlan> trainingPlan = trainingPlanRepository.findByIdAndUserId(trainingPlanId, user.get().getId());
                return trainingPlan.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound()
                                .header("Cause", "training plan not found")
                                .build()
                        );
            } else {
                return ResponseEntity.badRequest().header("Cause", "user not found").build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("Cause", "searching error").build();
        }
    }
}

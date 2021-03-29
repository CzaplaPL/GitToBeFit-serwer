package pl.umk.mat.git2befit.service.workout;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.user.entity.User;
import pl.umk.mat.git2befit.model.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;
import pl.umk.mat.git2befit.repository.user.UserRepository;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;
import pl.umk.mat.git2befit.repository.workout.TrainingPlanRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingPlanService {
    private final TrainingPlanRepository trainingPlanRepository;
    private final UserRepository userRepository;

    private final ExerciseRepository exerciseRepository;

    public TrainingPlanService(TrainingPlanRepository trainingPlanRepository, UserRepository userRepository, ExerciseRepository exerciseRepository) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
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

    public List<Exercise> getSimilarExercises(long id, TrainingForm trainingForm) throws IllegalArgumentException{
        Optional<Exercise> byId = exerciseRepository.findById(id);
        if (byId.isPresent()) {
            Exercise exerciseToExchange = byId.get();

            String trainingType = trainingForm.getTrainingType();
            String bodyPart = exerciseToExchange.getBodyPart().getName();
            List<Long> availableEquipmentIDs = trainingForm.getEquipmentIDs();

            List<Exercise> exercisesToReplace = exerciseRepository.getAllByBodyPart_NameAndTrainingTypes_Name(bodyPart, trainingType);

            exercisesToReplace = filterExercisesWithMatchingEquipment(availableEquipmentIDs, exercisesToReplace);
            return exercisesToReplace;
        }else {
            throw new IllegalArgumentException("Exercise with id: " + id + "is unknown");
        }
    }

    private List<Exercise> filterExercisesWithMatchingEquipment(List<Long> availableEquipmentIDs, List<Exercise> exercisesToReplace) {
        exercisesToReplace = exercisesToReplace.stream()
                .filter(exercise -> {
                    boolean temp;
                    for (Equipment equipment : exercise.getEquipmentsNeeded()) {
                        temp = availableEquipmentIDs.contains(equipment.getId());
                        if(!temp)
                            return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        return exercisesToReplace;
    }

    public void updateTrainingPlan(String title, Long id){
        if (trainingPlanRepository.existsById(id)) {
            TrainingPlan trainingPlan = trainingPlanRepository.findById(id).get();
            trainingPlan.setTitle(title);
            trainingPlanRepository.save(trainingPlan);
        }
        else
            throw new IllegalArgumentException("TrainingPlan with id: " + id + " is unknown");
    }
}

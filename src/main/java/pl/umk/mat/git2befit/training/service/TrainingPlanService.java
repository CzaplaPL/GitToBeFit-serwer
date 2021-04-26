package pl.umk.mat.git2befit.training.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.user.model.entity.User;
import pl.umk.mat.git2befit.training.model.equipment.Equipment;
import pl.umk.mat.git2befit.training.model.training.Exercise;
import pl.umk.mat.git2befit.training.model.training.TrainingForm;
import pl.umk.mat.git2befit.training.model.training.TrainingPlan;
import pl.umk.mat.git2befit.user.repository.UserRepository;
import pl.umk.mat.git2befit.training.repository.ExerciseRepository;
import pl.umk.mat.git2befit.training.repository.TrainingPlanRepository;
import pl.umk.mat.git2befit.user.service.JWTService;
import pl.umk.mat.git2befit.training.service.factory.TrainingPlanFacade;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingPlanService {
    private final TrainingPlanRepository trainingPlanRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingPlanFacade manufacture;
    private final Logger log = LoggerFactory.getLogger(TrainingPlanService.class);

    public TrainingPlanService(
            TrainingPlanRepository trainingPlanRepository,
            UserRepository userRepository,
            ExerciseRepository exerciseRepository,
            TrainingPlanFacade manufacture
    ) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.manufacture = manufacture;
    }

    public ResponseEntity<?> generate(TrainingForm trainingForm, String authorizationToken, String dateAsString) {
        TrainingPlan trainingPlan;
        try {
            LocalDateTime.parse(dateAsString);
            trainingPlan = manufacture.createTrainingPlan(trainingForm);
        } catch (DateTimeParseException exception) {
            return ResponseEntity.badRequest().header("Cause", "wrong date format").build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", e.getMessage()).build();
        }
        trainingPlan.setCreatedAt(dateAsString);
        List<TrainingPlan> savedTrainingPlan = null;
        if (authorizationToken != null) {
            try {
                String email = JWTService.parseEmail(authorizationToken);
                savedTrainingPlan = saveTrainingWithUserEmail(List.of(trainingPlan), email);
            } catch (Exception ignored) {
            }
        }
        TrainingPlan trainingPlanToReturn = savedTrainingPlan != null ? savedTrainingPlan.get(0) : trainingPlan;
        return ResponseEntity.ok(trainingPlanToReturn);
    }

    public List<TrainingPlan> saveTrainingWithUserEmail(List<TrainingPlan> trainingPlans, String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            trainingPlans.forEach(plan -> {
                plan.setUser(user.get());
                trainingPlanRepository.save(plan);
            });
            return trainingPlans;
        } else {
            throw new IllegalArgumentException("user not exist");
        }
    }

    public ResponseEntity<?> getAllTrainingPlansByUserEmail(String authorizationToken) {
        try {
            String email = JWTService.parseEmail(authorizationToken);
            return ResponseEntity.ok(trainingPlanRepository.findAllByUser_Email(email));
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Cause", "wrong token").build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("Cause", "server error").build();
        }
    }

    public ResponseEntity<?> getTrainingPlanByIdForUser(long trainingPlanId, String authorizationToken) {
        try {
            String userEmail = JWTService.parseEmail(authorizationToken);

            Optional<User> user = userRepository.findByEmail(userEmail);
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
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Cause", "wrong token").build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("Cause", "searching error").build();
        }
    }

    public List<Exercise> getSimilarExercises(long id, TrainingForm trainingForm) throws IllegalArgumentException {
        Optional<Exercise> byId = exerciseRepository.findById(id);
        if (byId.isPresent()) {
            Exercise exerciseToExchange = byId.get();

            String trainingType = trainingForm.getTrainingType();
            String bodyPart = exerciseToExchange.getBodyPart().getName();
            List<Long> availableEquipmentIDs = trainingForm.getEquipmentIDs();

            List<Exercise> exercisesToReplace = exerciseRepository.getAllByBodyPart_NameAndTrainingTypes_Name(bodyPart, trainingType);

            exercisesToReplace = filterExercisesWithMatchingEquipment(availableEquipmentIDs, exercisesToReplace);
            return exercisesToReplace;
        } else {
            throw new IllegalArgumentException("Exercise with id: " + id + "is unknown");
        }
    }

    private List<Exercise> filterExercisesWithMatchingEquipment(List<Long> availableEquipmentIDs, List<Exercise> exercisesToReplace) {
        exercisesToReplace = exercisesToReplace.stream()
                .filter(exercise -> {
                    boolean temp;
                    for (Equipment equipment : exercise.getEquipmentsNeeded()) {
                        temp = availableEquipmentIDs.contains(equipment.getId());
                        if (!temp)
                            return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        return exercisesToReplace;
    }

    public void updateTrainingPlan(String title, Long id) {
        Optional<TrainingPlan> trainingPlanOptional = trainingPlanRepository.findById(id);
        if (trainingPlanOptional.isPresent()) {
            TrainingPlan trainingPlan = trainingPlanOptional.get();
            trainingPlan.setTitle(title);
            trainingPlanRepository.save(trainingPlan);
        } else
            throw new IllegalArgumentException("TrainingPlan with id: " + id + " is unknown");
    }

    public ResponseEntity<?> delete(Long trainingPlanId, String authorizationToken) {
        try {
            String email = JWTService.parseEmail(authorizationToken);
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                Optional<TrainingPlan> trainingPlan = trainingPlanRepository.findById(trainingPlanId);
                if (trainingPlan.isEmpty())
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Cause", "training plan not found").build();
                if (canBeDeletedByUser(trainingPlan.get(), user.get())) {
                    trainingPlanRepository.delete(trainingPlan.get());
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Cause", "user not allowed").build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Cause", "user not found").build();
            }
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Cause", "wrong token").build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("Cause", "server error").build();
        }
    }

    private boolean canBeDeletedByUser(TrainingPlan trainingPlan, User user) {
        return trainingPlan.getUser().getId().equals(user.getId());
    }
}

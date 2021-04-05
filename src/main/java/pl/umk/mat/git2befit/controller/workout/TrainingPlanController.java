package pl.umk.mat.git2befit.controller.workout;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;
import pl.umk.mat.git2befit.service.user.JWTService;
import pl.umk.mat.git2befit.service.workout.TrainingPlanService;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanManufacture;

import java.util.List;

@RestController()
@RequestMapping("/training-plan")
public class TrainingPlanController {
    private final TrainingPlanManufacture manufacture;
    private final TrainingPlanService trainingPlanService;

    public TrainingPlanController(TrainingPlanManufacture manufacture, TrainingPlanService service) {
        this.manufacture = manufacture;
        this.trainingPlanService = service;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(
            @RequestBody(required = false) TrainingForm trainingForm,
            @RequestHeader(value = "Authorization", required = false) String authorizationToken
    ) {
        List<Training> trainingPlans;

        try {
            trainingPlans = manufacture.createTrainingPlan(trainingForm);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", e.getMessage()).build();
        }

        TrainingPlan trainingPlan = new TrainingPlan(trainingForm, trainingPlans);

        trainingPlan.setTitle(trainingForm.getTrainingType());
        List<TrainingPlan> savedTrainingPlan = null;
        if (authorizationToken != null) {
            try {
                String email = JWTService.parseEmail(authorizationToken);
                savedTrainingPlan = trainingPlanService.saveTrainingWithUserEmail(List.of(trainingPlan), email);
            } catch (Exception ignored) {
            }
        }

        return ResponseEntity.ok(savedTrainingPlan != null ? savedTrainingPlan.get(0) : trainingPlan);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(
            @RequestBody List<TrainingPlan> trainingPlan,
            @RequestHeader(value = "Authorization") String authorizationToken
    ) {
        String email = JWTService.parseEmail(authorizationToken);
        try {
            trainingPlanService.saveTrainingWithUserEmail(trainingPlan, email);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Cause", exception.getMessage()).build();
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public List<TrainingPlan> getAllTrainingPlansByUserId(@RequestHeader long userId) {
        return trainingPlanService.getAllTrainingPlansByUserId(userId);
    }

    @GetMapping("/{trainingPlanId}")
    public ResponseEntity<?> getOneTrainingPlansByUserId(
            @RequestHeader(value = "Authorization") String authorizationToken,
            @PathVariable long trainingPlanId
    ) {
        String email = JWTService.parseEmail(authorizationToken);
        return trainingPlanService.getTrainingPlanByIdForUser(trainingPlanId, email);
    }

    @PutMapping("/updateTitle/{id}")
    public ResponseEntity<?> updateTrainingPlan(@PathVariable Long id, @RequestHeader String title) {
        try {
            trainingPlanService.updateTrainingPlan(title, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().header("Cause", exception.getMessage()).build();
        }
    }
}

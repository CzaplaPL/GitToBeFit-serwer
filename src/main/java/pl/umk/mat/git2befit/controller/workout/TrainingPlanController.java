package pl.umk.mat.git2befit.controller.workout;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;
import pl.umk.mat.git2befit.service.user.JWTService;
import pl.umk.mat.git2befit.service.workout.TrainingPlanService;

import java.util.List;

@RestController()
@RequestMapping("/training-plan")
public class TrainingPlanController {
    private final TrainingPlanService trainingPlanService;

    public TrainingPlanController(TrainingPlanService service) {
        this.trainingPlanService = service;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(
            @RequestBody(required = false) TrainingForm trainingForm,
            @RequestHeader(value = "Authorization", required = false) String authorizationToken
    ) {
        return trainingPlanService.generate(trainingForm, authorizationToken);
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
    public ResponseEntity<?> getAllTrainingPlansByUserEmail(
            @RequestHeader(value = "Authorization") String authorizationToken
    ) {
        return trainingPlanService.getAllTrainingPlansByUserEmail(authorizationToken);
    }

    @GetMapping("/{trainingPlanId}")
    public ResponseEntity<?> getOneTrainingPlansByUserId(
            @RequestHeader(value = "Authorization") String authorizationToken,
            @PathVariable long trainingPlanId
    ) {
        return trainingPlanService.getTrainingPlanByIdForUser(trainingPlanId, authorizationToken);
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

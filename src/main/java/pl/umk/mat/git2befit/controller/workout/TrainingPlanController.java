package pl.umk.mat.git2befit.controller.workout;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.umk.mat.git2befit.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;
import pl.umk.mat.git2befit.service.user.JWTService;
import pl.umk.mat.git2befit.service.workout.TrainingPlanService;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanFacade;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.EXPECTATION_FAILED;

@RestController()
@RequestMapping("/training-plan")
public class TrainingPlanController {
    private final TrainingPlanFacade manufacture;
    private final TrainingPlanService trainingPlanService;

    public TrainingPlanController(TrainingPlanFacade manufacture, TrainingPlanService service) {
        this.manufacture = manufacture;
        this.trainingPlanService = service;
    }

    @ExceptionHandler(value = NotValidTrainingException.class)
    public ResponseEntity<?> handleValidationException(NotValidTrainingException exception) {
        return ResponseEntity.status(EXPECTATION_FAILED).header("Cause", exception.getMessage()).build();
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(
            @RequestBody TrainingForm trainingForm,
            @RequestHeader(value = "Authorization", required = false) String authorizationToken,
            @RequestHeader(value = "Date", required = false) String date
    ) {
        return trainingPlanService.generate(
                trainingForm,
                authorizationToken,
                date == null ? LocalDateTime.now().toString() : date
        );
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(
            @RequestBody List<TrainingPlan> trainingPlan,
            @RequestHeader(value = "Authorization") String authorizationToken
    ) {
        try {
            String email = JWTService.parseEmail(authorizationToken);
            trainingPlanService.saveTrainingWithUserEmail(trainingPlan, email);
            return ResponseEntity.ok().build();
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Cause", "wrong token").build();
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

    @DeleteMapping("/{trainingPlanId}")
    public ResponseEntity<?> deleteTrainingPlan(
            @PathVariable Long trainingPlanId,
            @RequestHeader(value = "Authorization") String authorizationToken
    ) {
        return trainingPlanService.delete(trainingPlanId, authorizationToken);
    }
}

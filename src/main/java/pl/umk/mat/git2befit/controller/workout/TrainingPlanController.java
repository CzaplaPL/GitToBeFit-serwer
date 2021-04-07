package pl.umk.mat.git2befit.controller.workout;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.umk.mat.git2befit.model.workout.conditions.BodyPart;
import pl.umk.mat.git2befit.model.workout.conditions.ExerciseForm;
import pl.umk.mat.git2befit.model.workout.conditions.TrainingType;
import pl.umk.mat.git2befit.model.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.workout.equipment.EquipmentType;
import pl.umk.mat.git2befit.model.workout.training.*;
import pl.umk.mat.git2befit.service.workout.TrainingPlanService;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanManufacture;

import java.util.ArrayList;
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
    public ResponseEntity<?> generate(@RequestBody(required = false) TrainingForm trainingForm) {
        List<ExerciseExecution> exerciseExecutions = new ArrayList<>();
        List<Training> trainingPlans;

        try {
            trainingPlans = manufacture.createTrainingPlan(trainingForm);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", e.getMessage()).build();
        }

        TrainingPlan trainingPlan = new TrainingPlan(trainingForm, trainingPlans);
        trainingPlan.setTitle(trainingForm.getTrainingType());
        return ResponseEntity.ok(trainingPlan);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(
            @RequestBody List<TrainingPlan> trainingPlan,
            @RequestHeader long userId
    ) {
        return trainingPlanService.save(trainingPlan, userId);
    }

    @GetMapping
    public List<TrainingPlan> getAllTrainingPlansByUserId(@RequestHeader long userId) {
        return trainingPlanService.getAllTrainingPlansByUserId(userId);
    }

    @GetMapping("/{trainingPlanId}")
    public ResponseEntity<?> getOneTrainingPlansByUserId(
            @RequestHeader long userId,
            @PathVariable long trainingPlanId
    ) {
        return trainingPlanService.getTrainingPlanByIdForUser(trainingPlanId, userId);
    }

    @PutMapping("/updateTitle/{id}")
    public ResponseEntity<?> updateTrainingPlan(@PathVariable Long id, @RequestHeader String title){
        try{
            trainingPlanService.updateTrainingPlan(title, id);
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException exception){
            return ResponseEntity.notFound().header("Cause", exception.getMessage()).build();
        }
    }
}

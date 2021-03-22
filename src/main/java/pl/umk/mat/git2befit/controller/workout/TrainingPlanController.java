package pl.umk.mat.git2befit.controller.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.model.workout.training.*;
import pl.umk.mat.git2befit.service.workout.TrainingService;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanManufacture;

import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("/training-plan")
public class TrainingPlanController {
    private TrainingPlanManufacture manufacture;
    private final TrainingService trainingService;

    public TrainingPlanController(TrainingPlanManufacture manufacture, TrainingService trainingService) {
        this.manufacture = manufacture;
        this.trainingService = trainingService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody(required = false) TrainingForm trainingForm){
        List<ExerciseExecution> exerciseExecutions = new ArrayList<>();
        List<Training> trainingPlans;
        try {
            trainingPlans = manufacture.createTrainingPlan(trainingForm);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", e.getMessage()).build();
        }

        TrainingPlan trainingPlan = new TrainingPlan(trainingForm, trainingPlans);
        return ResponseEntity.ok(trainingPlan);

    }

    @PostMapping("/modify/{id}")
    public ResponseEntity<?> modifyTrainingPlan(Exercise exercise, @RequestBody TrainingForm trainingForm){
        List<Exercise> similarExercises;
        try {
            similarExercises = trainingService.getSimilarExercises(exercise.getId(), trainingForm);
        }catch (IllegalArgumentException e){
            return ResponseEntity.notFound().header("Cause", e.getMessage()).build();
        }
        return ResponseEntity.ok().body(similarExercises);
    }
}

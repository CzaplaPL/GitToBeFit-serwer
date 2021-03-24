package pl.umk.mat.git2befit.controller.workout;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.service.workout.TrainingPlanService;

import java.util.List;

@RestController
public class ExerciseController {
    private final TrainingPlanService trainingPlanService;

    public ExerciseController(TrainingPlanService trainingPlanService) {
        this.trainingPlanService = trainingPlanService;
    }

    @PostMapping("/replace/{id}")
    public ResponseEntity<?> modifyTrainingPlan(@PathVariable Long id, @RequestBody TrainingForm trainingForm){
        List<Exercise> similarExercises;
        try {
            similarExercises = trainingPlanService.getSimilarExercises(id, trainingForm);
            return ResponseEntity.ok().body(similarExercises);
        }catch (IllegalArgumentException e){
            return ResponseEntity.notFound().header("Cause", e.getMessage()).build();
        }
    }
}

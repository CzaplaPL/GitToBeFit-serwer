package pl.umk.mat.git2befit.controller.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.service.workout.TrainingService;

import java.util.List;

@RestController
public class ExerciseController {
    private final TrainingService trainingService;

    public ExerciseController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("/replace-exercise/{id}")
    public ResponseEntity<?> modifyTrainingPlan(@PathVariable Long id, @RequestBody TrainingForm trainingForm){
        List<Exercise> similarExercises;
        try {
            similarExercises = trainingService.getSimilarExercises(id, trainingForm);
            return ResponseEntity.ok().body(similarExercises);
        }catch (IllegalArgumentException e){
            return ResponseEntity.notFound().header("Cause", e.getMessage()).build();
        }
    }
}

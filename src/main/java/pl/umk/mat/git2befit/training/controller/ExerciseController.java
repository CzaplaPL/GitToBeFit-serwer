package pl.umk.mat.git2befit.training.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.umk.mat.git2befit.training.model.training.Exercise;
import pl.umk.mat.git2befit.training.model.training.TrainingForm;
import pl.umk.mat.git2befit.training.service.TrainingPlanService;

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

    @GetMapping("/exercise")
    public ResponseEntity<?> getAllExercisesForSpecifiedEquipment(@RequestBody List<Long> equipmentIds) {
        try {
            return trainingPlanService.getAllExercisesForEquipmentIdsList(equipmentIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

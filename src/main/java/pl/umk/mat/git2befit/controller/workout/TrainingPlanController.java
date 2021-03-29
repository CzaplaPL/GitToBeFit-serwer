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
        List<Training> trainingPlans = new ArrayList<>();
        if (trainingForm == null) {
            for (int i = 1; i <= 9; i++) {
                if (i % 3 == 0) {
                    Training training = new Training();
                    training.setId(11);
                    training.setId(1);
                    training.setExercisesExecutions(exerciseExecutions);
                    exerciseExecutions.clear();
                    trainingPlans.add(training);
                }
                ExerciseExecution exerciseExecution = new ExerciseExecution();
                exerciseExecution.setExercise(new Exercise(i,
                        "cwiczenie" + i,
                        "start" + i,
                        "execution" + i,
                        "hints1 /n hint2"
                        , new ExerciseForm(i, "exerciseForm" + i)
                        , "videURl" + i, "photo" + i
                        , new BodyPart(i, "bodyPart" + i)
                        , List.of(new TrainingType(i, "TrainingType" + i))
                        , List.of(new Equipment(i, new EquipmentType(i, "kategoria" + i, "urlPhoto" + i)
                        , "name" + i, "urlVideo" + i)))
                );
                exerciseExecution.setCount(8);
                exerciseExecution.setSeries(3);
                exerciseExecutions.add(exerciseExecution);
                System.out.println(exerciseExecution.getExercise().getId());

            }
        } else {
            try {
                trainingPlans = manufacture.createTrainingPlan(trainingForm);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", e.getMessage()).build();
            }
        }

        TrainingPlan trainingPlan = new TrainingPlan(trainingForm, trainingPlans);
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

    @PutMapping("/updateTrainingPlanTitle/{id}")
    public ResponseEntity<?> updateTrainingPlan(@PathVariable Long id, @RequestHeader String title){
        try{
            trainingPlanService.updateTrainingPlan(title, id);
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException exception){
            return ResponseEntity.notFound().header("Cause", exception.getMessage()).build();
        }
    }
}

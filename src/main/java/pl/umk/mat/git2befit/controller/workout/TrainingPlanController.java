package pl.umk.mat.git2befit.controller.workout;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.model.entity.workout.Exercise;
import pl.umk.mat.git2befit.model.entity.workout.conditions.BodyPart;
import pl.umk.mat.git2befit.model.entity.workout.conditions.ExerciseForm;
import pl.umk.mat.git2befit.model.entity.workout.conditions.TrainingType;
import pl.umk.mat.git2befit.model.entity.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.entity.workout.equipment.EquipmentType;
import pl.umk.mat.git2befit.model.training.generation.factory.TrainingPlanManufacture;
import pl.umk.mat.git2befit.model.training.generation.model.ExerciseExecution;
import pl.umk.mat.git2befit.model.training.generation.model.Training;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;

import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("/training-plan")
public class TrainingPlanController {
    private TrainingPlanManufacture manufacture;

    public TrainingPlanController(TrainingPlanManufacture manufacture) {
        this.manufacture = manufacture;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody(required = false) TrainingForm trainingForm){
        List<ExerciseExecution> exerciseExecutions = new ArrayList<>();
        List<Training> trainingPlans = new ArrayList<>();
        if(trainingForm == null) {
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
        }else {
            trainingPlans = manufacture.createTrainingPlan(trainingForm);
        }

        TrainingPlan trainingPlan = new TrainingPlan(trainingForm, trainingPlans);
        return ResponseEntity.ok(trainingPlan);

    }
}

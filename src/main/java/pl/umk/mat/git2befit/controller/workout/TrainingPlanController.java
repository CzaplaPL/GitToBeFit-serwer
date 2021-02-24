package pl.umk.mat.git2befit.controller.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.model.entity.workout.Exercise;
import pl.umk.mat.git2befit.model.entity.workout.conditions.BodyPart;
import pl.umk.mat.git2befit.model.entity.workout.conditions.ScheduleType;
import pl.umk.mat.git2befit.model.entity.workout.conditions.TrainingType;
import pl.umk.mat.git2befit.model.entity.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.entity.workout.equipment.EquipmentType;
import pl.umk.mat.git2befit.model.training.generation.factory.TrainingPlanManufacture;
import pl.umk.mat.git2befit.model.training.generation.model.ExerciseExecution;
import pl.umk.mat.git2befit.model.training.generation.model.Training;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;
import pl.umk.mat.git2befit.repository.ExerciseRepository;

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
        List<TrainingPlan> trainingPlans = new ArrayList<>();

        for (int i = 1; i <= 9; i++) {
            if(i%3 == 0){
                TrainingPlan trainingPlan = new TrainingPlan();
                trainingPlan.setId(11);
                trainingPlan.setId(1);
                trainingPlan.setExercisesExecutions(exerciseExecutions);
                exerciseExecutions.clear();
                trainingPlans.add(trainingPlan);
            }
            ExerciseExecution exerciseExecution = new ExerciseExecution();
            exerciseExecution.setExercise(  new Exercise(i,
                    "cwiczenie" + i,
                    "start" + i,
                    "execution" + i,
                    "hints1 /n hint2"
                    , new ScheduleType(i, "exerciseForm" + i)
                    , "videURl" + i, "photo"+i
                    , new BodyPart(i, "bodyPart" + i)
                    , List.of(new TrainingType(i, "TrainingType" + i))
                    , List.of(new Equipment(i, new EquipmentType(i, "kategoria"+i, "urlPhoto" + i)
                    , "name" + i, "urlVideo" + i)))
            );
            exerciseExecution.setCount(8);
            exerciseExecution.setSeries(3);
            exerciseExecutions.add(exerciseExecution);
            System.out.println(exerciseExecution.getExercise().getId());

        }
        Training training = new Training(trainingForm, trainingPlans);
        System.out.println(trainingPlans.toString());

        //TrainingPlan trainingPlan = manufacture.createTrainingPlan(trainingForm);
        return ResponseEntity.ok(training);

    }
}

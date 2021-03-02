package pl.umk.mat.git2befit.controller.workout;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanManufacture;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;

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
        /*List<ExerciseExecution> exerciseExecutions = new ArrayList<>();
        List<TrainingPlan> trainingPlans = new ArrayList<>();

        for (int i = 1; i <= 9; i++) {
            if(i%3 == 0){
                TrainingPlan training = new TrainingPlan();
                training.setId(11);
                training.setId(1);
                training.setExercisesExecutions(exerciseExecutions);
                exerciseExecutions.clear();
                trainingPlans.add(training);
            }
            ExerciseExecution exerciseExecution = new ExerciseExecution();
            exerciseExecution.setExercise(  new Exercise(i,
                    "cwiczenie" + i,
                    "start" + i,
                    "execution" + i,
                    "hints1 /n hint2"
                    , new ExerciseForm(i, "exerciseForm" + i)
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
        TrainingPlan trainingPlan = new TrainingPlan(trainingForm, trainingPlans);
        System.out.println(trainingPlans.toString());*/

        List<Training> training = manufacture.createTrainingPlan(trainingForm);
        TrainingPlan trainingPlan = new TrainingPlan(trainingForm, training);
        return ResponseEntity.ok(trainingPlan);

    }
}

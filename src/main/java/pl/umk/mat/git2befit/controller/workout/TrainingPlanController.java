package pl.umk.mat.git2befit.controller.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.model.training.generation.factory.TrainingPlanFactory;
import pl.umk.mat.git2befit.model.training.generation.factory.TrainingPlanInterface;
import pl.umk.mat.git2befit.model.training.generation.factory.TrainingPlanManufacture;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;

@RestController("/training-plan")
public class TrainingPlanController {
    private TrainingPlanManufacture manufacture;


    public TrainingPlanController(TrainingPlanManufacture manufacture) {
        this.manufacture = manufacture;
    }

    @PostMapping("/")
    public ResponseEntity<?> generate(@RequestBody TrainingForm trainingForm){
        TrainingPlan trainingPlan = manufacture.createTrainingPlan(trainingForm);
        return ResponseEntity.ok(trainingPlan);
    }
}

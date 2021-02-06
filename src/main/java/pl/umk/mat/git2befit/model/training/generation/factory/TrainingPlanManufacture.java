package pl.umk.mat.git2befit.model.training.generation.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TrainingPlanManufacture {
    private TrainingPlanFactory trainingPlanFactory;

    @Autowired
    public TrainingPlanManufacture(TrainingPlanFactory trainingPlanFactory) {
        this.trainingPlanFactory = trainingPlanFactory;
    }

    public ResponseEntity<?> createTrainingPlan(){
        TrainingPlanInterface trainingPlanInterface = trainingPlanFactory.createPlan("SPLIT");
        return ResponseEntity.ok(trainingPlanInterface);
    }
}

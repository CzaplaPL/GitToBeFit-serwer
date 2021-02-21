package pl.umk.mat.git2befit.model.training.generation.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;

@Component
public class TrainingPlanManufacture {
    private TrainingPlanFactory trainingPlanFactory;

    @Autowired
    public TrainingPlanManufacture(TrainingPlanFactory trainingPlanFactory) {
        this.trainingPlanFactory = trainingPlanFactory;
    }

    public TrainingPlan createTrainingPlan(TrainingForm trainingForm){
        TrainingPlanInterface trainingPlan = trainingPlanFactory.createPlan(trainingForm.getTrainingType());
        return trainingPlan.create(trainingForm);
    }
}

package pl.umk.mat.git2befit.service.workout.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.validation.workout.TrainingValidator;
import pl.umk.mat.git2befit.validation.workout.ValidatorFactory;
import pl.umk.mat.git2befit.validation.TrainingFormValidationService;

import java.util.List;

@Component
public class TrainingPlanManufacture {
    private TrainingPlanFactory trainingPlanFactory;
    private ValidatorFactory validatorFactory;

    @Autowired
    public TrainingPlanManufacture(TrainingPlanFactory trainingPlanFactory, ValidatorFactory validatorFactory) {
        this.trainingPlanFactory = trainingPlanFactory;
        this.validatorFactory = validatorFactory;
    }

    public List<Training> createTrainingPlan(TrainingForm trainingForm) throws IllegalArgumentException{
        TrainingFormValidationService.validate(trainingForm);
        TrainingPlanInterface trainingPlan = trainingPlanFactory.createPlan(trainingForm.getTrainingType());
        TrainingValidator validator = validatorFactory.getValidator(trainingForm.getTrainingType());
        var trainingList = trainingPlan.create(trainingForm);
        validator.validateTraining(trainingList);
        return trainingList;
    }
}

package pl.umk.mat.git2befit.training.service.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.training.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.training.model.training.TrainingForm;
import pl.umk.mat.git2befit.training.model.training.TrainingPlan;
import pl.umk.mat.git2befit.training.validation.TrainingFormValidationService;

@Component
public class TrainingPlanFacade {
    private final TrainingPlanFactory trainingPlanFactory;

    @Autowired
    public TrainingPlanFacade(TrainingPlanFactory trainingPlanFactory) {
        this.trainingPlanFactory = trainingPlanFactory;
    }

    public TrainingPlan createTrainingPlan(TrainingForm trainingForm)
            throws IllegalArgumentException, NotValidTrainingException {
        TrainingFormValidationService.validate(trainingForm);

        TrainingPlanGenerator trainingPlanGenerator = trainingPlanFactory.createPlan(trainingForm.getTrainingType());

        TrainingPlan trainingPlan = trainingPlanGenerator.create(trainingForm);
        trainingPlanGenerator.validate(trainingPlan, trainingPlan.getTrainingForm());

        return trainingPlan;
    }
}

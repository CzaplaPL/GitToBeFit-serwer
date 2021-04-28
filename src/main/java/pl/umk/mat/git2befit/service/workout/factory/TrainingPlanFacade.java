package pl.umk.mat.git2befit.service.workout.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;
import pl.umk.mat.git2befit.validation.TrainingFormValidationService;

import java.util.List;

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

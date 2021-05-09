package pl.umk.mat.git2befit.training.service.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.training.exceptions.EquipmentCountException;
import pl.umk.mat.git2befit.training.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.training.model.training.TrainingForm;
import pl.umk.mat.git2befit.training.model.training.TrainingPlan;
import pl.umk.mat.git2befit.training.validation.TrainingFormValidationService;

@Component
public class TrainingPlanFacade {
    private final TrainingPlanFactory trainingPlanFactory;
    private final Logger log = LoggerFactory.getLogger(TrainingPlanFacade.class);

    @Autowired
    public TrainingPlanFacade(TrainingPlanFactory trainingPlanFactory) {
        this.trainingPlanFactory = trainingPlanFactory;
    }

    public TrainingPlan createTrainingPlan(TrainingForm trainingForm)
            throws IllegalArgumentException, NotValidTrainingException, EquipmentCountException {
        TrainingFormValidationService.validate(trainingForm);

        TrainingPlanGenerator trainingPlanGenerator = trainingPlanFactory.createPlan(trainingForm.getTrainingType());

        TrainingPlan trainingPlan = trainingPlanGenerator.create(trainingForm);
        log.debug("");

        trainingPlanGenerator.validate(trainingPlan, trainingPlan.getTrainingForm());

        return trainingPlan;
    }
}

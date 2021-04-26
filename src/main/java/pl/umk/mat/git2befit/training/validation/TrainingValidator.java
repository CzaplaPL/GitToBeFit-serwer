package pl.umk.mat.git2befit.training.validation;

import pl.umk.mat.git2befit.training.model.training.TrainingForm;
import pl.umk.mat.git2befit.training.model.training.TrainingPlan;

public interface TrainingValidator {
    void validate(TrainingPlan trainingList, TrainingForm form);
}

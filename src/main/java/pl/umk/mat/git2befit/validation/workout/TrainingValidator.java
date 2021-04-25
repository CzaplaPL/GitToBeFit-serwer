package pl.umk.mat.git2befit.validation.workout;

import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;

import java.util.List;

public interface TrainingValidator {
    void validate(TrainingPlan trainingList, TrainingForm form);
}

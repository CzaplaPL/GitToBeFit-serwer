package pl.umk.mat.git2befit.validation.workout;

import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;

import java.util.List;

public interface TrainingValidator {
    void validate(List<Training> trainingList, TrainingForm form);
}

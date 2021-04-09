package pl.umk.mat.git2befit.validation.workout;

import pl.umk.mat.git2befit.model.workout.training.Training;

import java.util.List;

public interface TrainingValidator {
    void validateTraining(List<Training> trainingList);
}
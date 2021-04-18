package pl.umk.mat.git2befit.validation.workout;

import pl.umk.mat.git2befit.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;

import java.util.List;

public class FitnessValidator implements TrainingValidator {
    @Override
    public void validate(List<Training> trainingList, TrainingForm form) throws NotValidTrainingException {
        for (Training training : trainingList) {
            // walidacja czasu
            List<ExerciseExecution> exercisesExecutions = training.getExercisesExecutions();

            int size = exercisesExecutions.size();
            if (size * 3 != form.getDuration())
                throw new NotValidTrainingException("wrong exercises count");

            long count = exercisesExecutions
                    .stream()
                    .map(exerciseExecution -> exerciseExecution.getExercise().getId())
                    .distinct()
                    .count();
            if (count != exercisesExecutions.size())
                throw new NotValidTrainingException("duplicated exercises");


        }
    }
}

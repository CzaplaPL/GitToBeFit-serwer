package pl.umk.mat.git2befit.validation.workout;

import pl.umk.mat.git2befit.training.model.conditions.BodyPart;
import pl.umk.mat.git2befit.training.model.conditions.ExerciseForm;
import pl.umk.mat.git2befit.training.model.conditions.TrainingType;
import pl.umk.mat.git2befit.training.model.equipment.Equipment;
import pl.umk.mat.git2befit.training.model.training.Exercise;
import pl.umk.mat.git2befit.training.model.training.ExerciseExecution;
import pl.umk.mat.git2befit.training.model.training.Training;
import pl.umk.mat.git2befit.training.model.training.TrainingForm;

import java.util.ArrayList;
import java.util.List;

public class TrainingValidationTestCase {

    protected TrainingForm getValidTrainingForm(
            String trainingType,
            List<String> bodyParts,
            int duration
    ) {
        TrainingForm trainingForm = new TrainingForm();
        trainingForm.setTrainingType(trainingType);
        trainingForm.setBodyParts(bodyParts);
        trainingForm.setDaysCount(0);
        trainingForm.setScheduleType("CIRCUIT");
        trainingForm.setDuration(duration);
        return trainingForm;
    }

    protected List<Training> getListOfTrainings(int countOfExercises) {
        List<Training> list = new ArrayList<>();
        Training training = new Training();
        List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();
        for (int i = 0; i < countOfExercises; i++) {
            exerciseExecutionList.add(getNewExerciseExecution(i + 1, String.format("Cwiczenie %d", i + 1), "none"));
        }
        training.setExercisesExecutions(exerciseExecutionList);
        training.setBreakTime(0);
        training.setCircuitsCount(10);
        list.add(training);
        return list;
    }

    protected ExerciseExecution getNewExerciseExecution(
            long id,
            String name,
            String bodyPart
    ) {
        ExerciseExecution execution = new ExerciseExecution();
        execution.setExercise(new Exercise(
                id,
                name,
                "Description",
                "Correct exec",
                "Hints",
                new ExerciseForm(69, "Forma jakastam"),
                "url",
                "url2",
                new BodyPart(2115, bodyPart),
                List.of(new TrainingType()),
                List.of(new Equipment())
        ));
        execution.setCount(8);
        execution.setSeries(8);
        execution.setTime(8);
        return execution;
    }
}

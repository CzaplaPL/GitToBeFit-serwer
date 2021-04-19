package pl.umk.mat.git2befit.validation.workout;

import org.junit.jupiter.api.Test;
import pl.umk.mat.git2befit.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FitnessValidatorTest extends TrainingValidationTestCase {
    private final FitnessValidator validator = new FitnessValidator();

    @Test
    public void fitnessValidatorShouldThrowExceptionWhenTrainingLengthIsNotEqualWithForm() {
        //given
        TrainingForm form = getValidTrainingForm("FITNESS", List.of(), 9);
        List<Training> list = getListOfTrainings(4);

        //when & then
        NotValidTrainingException exception = assertThrows(
                NotValidTrainingException.class,
                () -> validator.validate(list, form)
        );

        assertThat(exception.getMessage(), is("wrong exercises count"));
    }

    @Test
    public void fitnessValidatorShouldThrowExceptionWhenTrainingWillContainDuplicates() {
        //given
        TrainingForm form = getValidTrainingForm("FITNESS", List.of(), 9);
        List<Training> list = getListOfTrainings(2);
        List<ExerciseExecution> exec = list.get(0).getExercisesExecutions();
        exec.add(exec.get(0));

        //when & then
        NotValidTrainingException exception = assertThrows(
                NotValidTrainingException.class,
                () -> validator.validate(list, form)
        );

        assertThat(exception.getMessage(), is("duplicated exercises"));
    }

    @Test
    public void fitnessValidatorShouldThrowExceptionWhenExerciseWouldNotBeForExactBodyPart() {
        // given
        TrainingForm form = getValidTrainingForm(
                "FITNESS",
                List.of("SIXPACK", "CHEST", "BICEPS"),
                15
        );
        List<Training> listOfTrainings = getListOfTrainings(4);
        Training training = listOfTrainings.get(0);
        ExerciseExecution wrongExerciseExecution = getNewExerciseExecution(
                999,
                "Cwiczenie wlasne",
                "SHOULDERS"
        );
        training.getExercisesExecutions().add(wrongExerciseExecution);

        //when & then
        NotValidTrainingException exception = assertThrows(
                NotValidTrainingException.class,
                () -> validator.validate(listOfTrainings, form)
        );

        assertThat(exception.getMessage(), is("wrong exercise"));
    }
}
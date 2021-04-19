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

class CardioValidatorTest extends TrainingValidationTestCase {

    @Test
    public void cardioValidatorShouldThrowExceptionWhenTrainingLengthIsNotEqualWithForm() {
        //given
        CardioValidator validator = new CardioValidator();
        TrainingForm form = getValidTrainingForm("CARDIO", List.of(), 9);
        List<Training> list = getListOfTrainings(4);

        //when & then
        NotValidTrainingException exception = assertThrows(
                NotValidTrainingException.class,
                () -> validator.validate(list, form)
        );

        assertThat(exception.getMessage(), is("wrong exercises count"));
    }

    @Test
    public void cardioValidatorShouldThrowExceptionWhenTrainingWillContainDuplicates() {
        //given
        CardioValidator validator = new CardioValidator();
        TrainingForm form = getValidTrainingForm("CARDIO", List.of(), 9);
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
}
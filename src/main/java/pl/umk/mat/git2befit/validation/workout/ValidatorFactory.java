package pl.umk.mat.git2befit.validation.workout;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.validation.workout.implementation.CardioValidator;
import pl.umk.mat.git2befit.validation.workout.implementation.FBWValidator;
import pl.umk.mat.git2befit.validation.workout.implementation.FitnessValidator;
import pl.umk.mat.git2befit.validation.workout.implementation.SplitValidator;

@Component
public class ValidatorFactory {
    public TrainingValidator getValidator(String trainingType) throws IllegalArgumentException {

        return switch (trainingType.toUpperCase()) {
            case "SPLIT" -> new SplitValidator();
            case "FBW" -> new FBWValidator();
            case "CARDIO" -> new CardioValidator();
            case "FITNESS" -> new FitnessValidator();
            default -> throw new IllegalArgumentException("");
        };
    }
}
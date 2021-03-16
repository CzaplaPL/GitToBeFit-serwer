package pl.umk.mat.git2befit.service.workout.factory;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.service.workout.factory.implementation.CardioTrainingPlan;
import pl.umk.mat.git2befit.service.workout.factory.implementation.FBWTrainingPlan;
import pl.umk.mat.git2befit.service.workout.factory.implementation.FitnessTrainingPlan;
import pl.umk.mat.git2befit.service.workout.factory.implementation.SplitTrainingPlan;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;

@Component
public class TrainingPlanFactory {
    public TrainingPlanFactory(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    private ExerciseRepository exerciseRepository;

    public TrainingPlanInterface createPlan(String trainingType) throws IllegalArgumentException{

        return switch (trainingType.toUpperCase()) {
            case "SPLIT" -> new SplitTrainingPlan(exerciseRepository);
            case "FBW" -> new FBWTrainingPlan(exerciseRepository);
            case "CARDIO" -> new CardioTrainingPlan(exerciseRepository);
            case "FITNESS" -> new FitnessTrainingPlan(exerciseRepository);
            default -> throw new IllegalArgumentException("");
        };
    }
}

package pl.umk.mat.git2befit.model.training.generation.factory;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.entity.workout.Exercise;
import pl.umk.mat.git2befit.repository.ExerciseRepository;

@Component
public class TrainingPlanFactory {
    public TrainingPlanFactory(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    private ExerciseRepository exerciseRepository;


    public TrainingPlanInterface createPlan(String trainingType) throws IllegalArgumentException{

        return switch (trainingType) {
            case "SPLIT" -> new SplitTrainingPlan(exerciseRepository);
            case "FBW" -> new FBWTrainingPlan();
            case "CARDIO" -> new CardioTrainingPlan();
            case "FITNESS" -> new FitnessTrainingPlan();
            default -> throw new IllegalArgumentException("");
        };
    }
}

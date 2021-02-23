package pl.umk.mat.git2befit.model.training.generation.factory;

import org.springframework.stereotype.Component;

@Component
public class TrainingPlanFactory {

    public TrainingPlanInterface createPlan(String trainingType) throws IllegalArgumentException{

        return switch (trainingType) {
            case "SPLIT" -> new SplitTrainingPlan();
            case "FBW" -> new FBWTrainingPlan();
            case "CARDIO" -> new CardioTrainingPlan();
            case "FITNESS" -> new FitnessTrainingPlan();
            default -> throw new IllegalArgumentException("");
        };
    }
}
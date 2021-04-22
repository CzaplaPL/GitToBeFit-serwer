package pl.umk.mat.git2befit.service.workout.newworkout;

import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;

public class TrainingPlanGenerator {
    private final TrainingPlanGeneration trainingPlanGeneration;

    public TrainingPlanGenerator(TrainingPlanGeneration trainingPlanGeneration) {
        this.trainingPlanGeneration = trainingPlanGeneration;
    }

    public TrainingPlan getValidTraining(TrainingForm form) {
        TrainingPlan plan = trainingPlanGeneration.createPlan(form);
        trainingPlanGeneration.validate(plan, form);
        return plan;
    }
}

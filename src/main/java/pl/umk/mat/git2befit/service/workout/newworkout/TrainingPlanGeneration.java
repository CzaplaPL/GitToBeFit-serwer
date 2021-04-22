package pl.umk.mat.git2befit.service.workout.newworkout;

import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;

interface TrainingPlanGeneration {
    TrainingPlan createPlan(TrainingForm trainingForm);

    void validate(TrainingPlan trainingPlan, TrainingForm trainingForm);
}

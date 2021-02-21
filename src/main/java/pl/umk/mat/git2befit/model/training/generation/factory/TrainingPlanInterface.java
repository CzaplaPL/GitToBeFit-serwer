package pl.umk.mat.git2befit.model.training.generation.factory;

import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;

public interface TrainingPlanInterface {
    TrainingPlan create(TrainingForm trainingForm);
}

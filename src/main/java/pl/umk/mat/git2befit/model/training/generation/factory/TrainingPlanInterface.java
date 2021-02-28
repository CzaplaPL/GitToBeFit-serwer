package pl.umk.mat.git2befit.model.training.generation.factory;

import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;

import java.util.List;

public interface TrainingPlanInterface {
    List<TrainingPlan> create(TrainingForm trainingForm);
}

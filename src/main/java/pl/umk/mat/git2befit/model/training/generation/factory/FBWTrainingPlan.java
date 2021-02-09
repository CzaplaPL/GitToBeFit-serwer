package pl.umk.mat.git2befit.model.training.generation.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;
import pl.umk.mat.git2befit.repository.ExerciseRepository;
@Component
public class FBWTrainingPlan implements TrainingPlanInterface {
    @Autowired
    private ExerciseRepository exerciseRepository;

    @Override
    public TrainingPlan create(TrainingForm trainingForm) {
        return null;
    }
}

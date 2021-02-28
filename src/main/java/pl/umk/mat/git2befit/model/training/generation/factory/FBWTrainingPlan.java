package pl.umk.mat.git2befit.model.training.generation.factory;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.training.generation.model.Training;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;
import pl.umk.mat.git2befit.repository.ExerciseRepository;

import java.util.List;

@Component
public class FBWTrainingPlan implements TrainingPlanInterface {
    private final ExerciseRepository exerciseRepository;

    public FBWTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public List<Training> create(TrainingForm trainingForm) {
        return null;
    }
}

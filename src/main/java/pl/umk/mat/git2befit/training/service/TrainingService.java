package pl.umk.mat.git2befit.training.service;

import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.training.repository.ExerciseRepository;

@Service
public class TrainingService {
    private final ExerciseRepository exerciseRepository;

    public TrainingService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }


}

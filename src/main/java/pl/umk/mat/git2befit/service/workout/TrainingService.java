package pl.umk.mat.git2befit.service.workout;

import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingService {
    private final ExerciseRepository exerciseRepository;

    public TrainingService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }


}

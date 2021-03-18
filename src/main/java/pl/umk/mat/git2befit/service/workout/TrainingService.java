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

    public List<Exercise> getSimilarExercises(long id, TrainingForm trainingForm) throws IllegalArgumentException{
        Optional<Exercise> byId = exerciseRepository.findById(id);
        if (byId.isPresent()) {
            Exercise exerciseToExchange = byId.get();

            String trainingType = trainingForm.getTrainingType();
            String bodyPart = exerciseToExchange.getBodyPart().getName();
            List<Long> availableEquipmentIDs = trainingForm.getEquipmentIDs();

            List<Exercise> exercisesToReplace = exerciseRepository.getAllByBodyPart_NameAndTrainingTypes_Name(bodyPart, trainingType);

            exercisesToReplace = filterExercisesWithMatchingEquipment(availableEquipmentIDs, exercisesToReplace);
            return exercisesToReplace;
        }else {
            throw new IllegalArgumentException("Exercise with id: " + id + "is unknown");
        }
    }

    private List<Exercise> filterExercisesWithMatchingEquipment(List<Long> availableEquipmentIDs, List<Exercise> exercisesToReplace) {
        exercisesToReplace = exercisesToReplace.stream()
                .filter(exercise -> {
                    boolean temp = false;
                    for (Equipment equipment : exercise.getEquipmentsNeeded()) {
                        temp = availableEquipmentIDs.contains(equipment.getId());
                    }
                    return temp;
                })
                .collect(Collectors.toList());
        return exercisesToReplace;
    }
}

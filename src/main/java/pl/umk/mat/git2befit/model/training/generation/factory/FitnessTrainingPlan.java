package pl.umk.mat.git2befit.model.training.generation.factory;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.entity.workout.Exercise;
import pl.umk.mat.git2befit.model.training.generation.model.Training;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;
import pl.umk.mat.git2befit.repository.ExerciseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class FitnessTrainingPlan implements TrainingPlanInterface {
    private static final String TRAINING_TYPE = "FITNESS";
    private static final int SINGLE_STEP = 3;
    private static final List<String> BODY_PARTS = List.of("CHEST", "SIXPACK", "BACK",  "LEGS", "ARMS");

    private final ExerciseRepository exerciseRepository;

    private List<Exercise> allExercises;

    public FitnessTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public List<Training> create(TrainingForm trainingForm) {
        this.allExercises = exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
        List<Exercise> filteredListOfExercises = getFilteredListOfExercises(trainingForm.getEquipmentIDs());

        int duration = trainingForm.getDuration();
        int exercisesToGet = duration / SINGLE_STEP;
        ThreadLocalRandom randomIndexGen = ThreadLocalRandom.current();
        Training training = new Training();
        List<Exercise> rolledExercises = new ArrayList<>();
        for (String bodyPart : BODY_PARTS) {
            List<Exercise> exercisesForSpecifiedBodyPart = getExercisesForSpecifiedBodyPart(filteredListOfExercises, bodyPart);
            if (!exercisesForSpecifiedBodyPart.isEmpty()) {
//                randomIndexGen.nextInt(exercisesForSpecifiedBodyPart.get())
            }
        }
        return List.of(training);
    }

    private List<Exercise> getFilteredListOfExercises(List<Long> equipmentIndexList) {
        Map<Long, Exercise> exercisesMap = allExercises.stream()
                .collect(Collectors.toMap(Exercise::getId, exercise -> exercise));
        return equipmentIndexList.stream().map(exercisesMap::get).collect(Collectors.toList());
    }

    private List<Exercise> getExercisesForSpecifiedBodyPart(List<Exercise> exercises, String bodyPartName) {
        return exercises.stream()
                .filter(exercise -> exercise.getBodyPart().getName().toUpperCase().equals(bodyPartName))
                .collect(Collectors.toList());
    }
}

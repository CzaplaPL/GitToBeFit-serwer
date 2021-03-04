package pl.umk.mat.git2befit.service.workout.factory.implementation;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanInterface;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class FitnessTrainingPlan implements TrainingPlanInterface {
    private static final String TRAINING_TYPE = "FITNESS";
    private static final int SINGLE_STEP = 3;
    private static final List<String> ALL_BODY_PARTS = List.of("CHEST", "SIXPACK", "BACK", "LEGS", "ARMS");

    private final ExerciseRepository exerciseRepository;

    private List<Exercise> allExercises;

    public FitnessTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public List<Training> create(TrainingForm trainingForm) {
        this.allExercises = exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
        List<Exercise> filteredListOfExercises = filterAllByAvailableEquipment(allExercises, trainingForm.getEquipmentIDs());

        int duration = trainingForm.getDuration();
        int exercisesToGet = duration / SINGLE_STEP;
        ThreadLocalRandom randomIndexGen = ThreadLocalRandom.current();
        Training training = new Training();
        List<String> bodyPartsFromForm = trainingForm.getBodyParts().isEmpty() ? ALL_BODY_PARTS : trainingForm.getBodyParts();
        List<Exercise> rolledExercises = new ArrayList<>();
        int noExerciseCounter = 0;
        for (String bodyPart : bodyPartsFromForm) {
            List<Exercise> exercisesForSpecifiedBodyPart = getExercisesForSpecifiedBodyPart(filteredListOfExercises, bodyPart);
            if (!exercisesForSpecifiedBodyPart.isEmpty()) {
                int random = randomIndexGen.nextInt(exercisesForSpecifiedBodyPart.size());
                rolledExercises.add(exercisesForSpecifiedBodyPart.get(random));
                filteredListOfExercises.remove(random);
            } else {
                noExerciseCounter++;
            }
        }
        if (noExerciseCounter == bodyPartsFromForm.size()) {
            //todo coś zrobić że nie ma ćwiczeń na żadną partię ciała
        } else {
            noExerciseCounter = 0;
            int tempIndex = 0;
            while (rolledExercises.size() == exercisesToGet) {
                List<Exercise> exercisesForSpecifiedBodyPart = getExercisesForSpecifiedBodyPart(filteredListOfExercises,
                        bodyPartsFromForm.get(tempIndex));
                if (!exercisesForSpecifiedBodyPart.isEmpty()) {
                    int random = randomIndexGen.nextInt(exercisesForSpecifiedBodyPart.size());
                    rolledExercises.add(exercisesForSpecifiedBodyPart.get(random));
                    filteredListOfExercises.remove(random);
                } else {
                    noExerciseCounter++;
                }
                if (tempIndex < bodyPartsFromForm.size()) {
                    tempIndex++;
                } else {
                    tempIndex = 0;
                }
                if (noExerciseCounter == exercisesToGet) {
                    break;
                }
            }
            List<ExerciseExecution> exercisesExecutions = getExercisesExecutions(rolledExercises);
            training.setExercisesExecutions(exercisesExecutions);
            return List.of(training);
        }
        return Collections.emptyList();
    }

    private List<Exercise> getExercisesForSpecifiedBodyPart(List<Exercise> exercises, String bodyPartName) {
        return exercises.stream()
                .filter(exercise -> exercise.getBodyPart().getName().toUpperCase().equals(bodyPartName))
                .collect(Collectors.toList());
    }

    private List<ExerciseExecution> getExercisesExecutions(List<Exercise> rolledExercises) {
        List<ExerciseExecution> execList = new ArrayList<>();
        for (Exercise exercise : rolledExercises) {
            String scheduleType = exercise.getScheduleType().getName().toUpperCase();
            ExerciseExecution exerciseExecution = new ExerciseExecution();
            // powtorzenia
            exerciseExecution.setExercise(exercise);
            if (scheduleType.equals("REPEAT")) {
                exerciseExecution.setSeries(3);
                exerciseExecution.setCount(8);
                exerciseExecution.setTime(0);
            } else if (scheduleType.equals("TIME")) {
                exerciseExecution.setCount(0);
                exerciseExecution.setSeries(3);
                exerciseExecution.setTime(30); // w sekundach
            }
            execList.add(exerciseExecution);
        }
        return execList;
    }
}

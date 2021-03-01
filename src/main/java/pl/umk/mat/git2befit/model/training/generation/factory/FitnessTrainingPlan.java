package pl.umk.mat.git2befit.model.training.generation.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.entity.workout.Exercise;
import pl.umk.mat.git2befit.model.entity.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.training.generation.model.Training;
import pl.umk.mat.git2befit.model.training.generation.model.ExerciseExecution;
import pl.umk.mat.git2befit.model.training.generation.model.Training;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;
import pl.umk.mat.git2befit.repository.ExerciseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class FitnessTrainingPlan implements TrainingPlanInterface {
    private static final String TRAINING_TYPE = "FITNESS";
    private static final int SINGLE_STEP = 3;
    private static final List<String> BODY_PARTS = List.of("CHEST", "SIXPACK", "BACK", "LEGS", "ARMS");

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
                int random = randomIndexGen.nextInt(exercisesForSpecifiedBodyPart.size());
                rolledExercises.add(exercisesForSpecifiedBodyPart.get(random));
            }
        }
        List<ExerciseExecution> exercisesExecutions = new ArrayList<>();
        String scheduleType = trainingForm.getScheduleType().toUpperCase();
        switch (scheduleType) {
            case "SERIES" -> exercisesExecutions = getExercisesExecutionsWithSeries(rolledExercises);
            case "CIRCUIT" -> {
                exercisesExecutions = getExercisesExecutionsInCircuit(rolledExercises);
            }
        }
        training.setExercisesExecutions(exercisesExecutions);
        return List.of(training);
    }

    private List<Exercise> getFilteredListOfExercises(List<Long> equipmentIndexList) {
        return allExercises.stream()
                .filter(exercise -> exercise.getEquipmentsNeeded().stream()
                    .map(equipment -> equipment.getId())
                    .anyMatch(aLong -> equipmentIndexList.contains(aLong)))
                .collect(Collectors.toList());
        /*Map<Long, Exercise> exercisesMap = allExercises.stream()
                .collect(Collectors.toMap(Exercise::getId, exercise -> exercise));
        return equipmentIndexList.stream().map(exercisesMap::get).collect(Collectors.toList());*/
    }

    private List<Exercise> getExercisesForSpecifiedBodyPart(List<Exercise> exercises, String bodyPartName) {
        return exercises.stream()
                .filter(exercise -> exercise.getBodyPart().getName().toUpperCase().equals(bodyPartName))
                .collect(Collectors.toList());
    }

    private List<ExerciseExecution> getExercisesExecutionsInCircuit(List<Exercise> rolledExercises) {
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

    private List<ExerciseExecution> getExercisesExecutionsWithSeries(List<Exercise> rolledExercises) {
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
                exerciseExecution.setSeries(0);
                exerciseExecution.setCount(3);
                exerciseExecution.setTime(30);
            }
            execList.add(exerciseExecution);
        }
        return execList;
    }
}

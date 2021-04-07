package pl.umk.mat.git2befit.service.workout.factory.implementation;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanInterface;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SplitTrainingPlan implements TrainingPlanInterface {
    private final String TRAINING_TYPE = "SPLIT";
    private final List<String> smallBodyParts = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS", "SHOULDERS");
    private final List<String> bigBodyParts = List.of("CHEST", "BACK", "THIGHS");

    private final ExerciseRepository exerciseRepository;

    private TrainingForm trainingForm;


    public SplitTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }


    @Override
    public List<Training> create(TrainingForm trainingForm) {
        this.trainingForm = trainingForm;
        List<Exercise> exerciseListFilteredByTrainingType = getExerciseListFilteredByTrainingType();
        List<Exercise> exercisesWithoutEquipment = exerciseRepository.getAllWithNoEquipmentForTrainingTypeName(TRAINING_TYPE);
        List<Exercise> exercisesWithEquipment = filterAllByAvailableEquipment(exerciseListFilteredByTrainingType, trainingForm.getEquipmentIDs());

        Map<String, List<ExerciseExecution>> trainingForBodyPart = assignExercisesToBodyPart(exercisesWithEquipment, exercisesWithoutEquipment);

        return divideTrainingIntoDays(trainingForBodyPart);
    }


    private Training getTrainingForDay(Map<String, List<ExerciseExecution>> trainingForBodyPart, String ... bodyParts) {
        Training training = new Training();
        for (String part: bodyParts){
            if(trainingForBodyPart.containsKey(part)) {
                List<ExerciseExecution> exerciseExecutions = trainingForBodyPart.get(part);
                training.addExerciseExecution(exerciseExecutions);
            }
        }
        return training;
    }

    private List<Exercise> getExerciseListFilteredByTrainingType() {
        return exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
    }

    private Map<String, List<ExerciseExecution>> assignExercisesToBodyPart(List<Exercise> exercisesWithEquipment, List<Exercise> exercisesWithoutEquipment) {
        List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();
        Map<String, List<ExerciseExecution>> trainingForBodyPart = new HashMap<>();

        List<String> trainingFormBodyParts = trainingForm.getBodyParts();

        for (String bodyPart : trainingFormBodyParts) {

            List<Exercise> exercisesWithEquipmentFiltered = getExercisesFilteredByBodyPart(exercisesWithEquipment, bodyPart);
            List<Exercise> exercisesWithoutEquipmentFiltered = getExercisesFilteredByBodyPart(exercisesWithoutEquipment, bodyPart);

            int amountOfExercises = getAmountOfExercisesForBodyPart(bodyPart);

            for (int i = 0; i < amountOfExercises; i++) {
                try {
                    ExerciseExecution exerciseExecution = getUniqueExercise(exercisesWithEquipmentFiltered, exercisesWithoutEquipmentFiltered);
                    exerciseExecutionList.add(exerciseExecution);
                }catch (IllegalStateException ignore){}
            }

            trainingForBodyPart.put(bodyPart, exerciseExecutionList);
            exerciseExecutionList = new ArrayList<>();
        }
        return trainingForBodyPart;
    }

    private List<Exercise> getExercisesFilteredByBodyPart(List<Exercise> exercises, String bodyPart) {
        return exercises.stream()
                .filter(exercise -> exercise.getBodyPart().getName().equals(bodyPart))
                .collect(Collectors.toList());
    }

    private int getAmountOfExercisesForBodyPart(String bodyPart) {
        int amountOfExercises;
        if (smallBodyParts.contains(bodyPart))
            amountOfExercises = 3;
        else
            amountOfExercises = 4;
        return amountOfExercises;
    }

    private ExerciseExecution getUniqueExercise(List<Exercise> exercisesWithEquipmentFiltered, List<Exercise> exercisesWithoutEquipmentFiltered) throws IllegalStateException {
        ExerciseExecution exerciseExecution = new ExerciseExecution();
        Random random = new Random();
        int randomInt;

        if (isEnoughExercises(exercisesWithEquipmentFiltered)) {
            randomInt = random.nextInt(exercisesWithEquipmentFiltered.size());
            exerciseExecution.setExercise(exercisesWithEquipmentFiltered.get(randomInt));
            exercisesWithEquipmentFiltered.remove(randomInt);
        } else if (isEnoughExercises(exercisesWithoutEquipmentFiltered)) {
            randomInt = random.nextInt(exercisesWithoutEquipmentFiltered.size());
            exerciseExecution.setExercise(exercisesWithoutEquipmentFiltered.get(randomInt));
            exercisesWithoutEquipmentFiltered.remove(randomInt);
        }else {
            throw new IllegalStateException();
        }

        exerciseExecution.setSeries(3);
        exerciseExecution.setCount(8);

        return exerciseExecution;
    }

    private boolean isEnoughExercises(List<Exercise> exercises) {
        return exercises.size() != 0;
    }

    private List<Training> divideTrainingIntoDays(Map<String, List<ExerciseExecution>> trainingForBodyPart) {
        int daysCount = trainingForm.getDaysCount();
        List<Training> trainingList = new ArrayList<>();

        switch (daysCount){
            case 3 -> {
                trainingList.add(getTrainingForDay(trainingForBodyPart, "CHEST","BICEPS", "SIXPACK"));
                trainingList.add(getTrainingForDay(trainingForBodyPart, "BACK", "CALVES"));
                trainingList.add(getTrainingForDay(trainingForBodyPart, "THIGHS", "TRICEPS", "SHOULDERS"));
            }
            case 4 -> {
                trainingList.add(getTrainingForDay(trainingForBodyPart, "CHEST","BICEPS" ));
                trainingList.add(getTrainingForDay(trainingForBodyPart, "BACK", "CALVES"));
                trainingList.add(getTrainingForDay(trainingForBodyPart, "THIGHS", "TRICEPS"));
                trainingList.add(getTrainingForDay(trainingForBodyPart, "SIXPACK","SHOULDERS"));
            }
            case 5-> {
                trainingList.add(getTrainingForDay(trainingForBodyPart, "CHEST","BICEPS" ));
                trainingList.add(getTrainingForDay(trainingForBodyPart, "BACK"));
                trainingList.add(getTrainingForDay(trainingForBodyPart, "THIGHS"));
                trainingList.add(getTrainingForDay(trainingForBodyPart, "SIXPACK","SHOULDERS"));
                trainingList.add(getTrainingForDay(trainingForBodyPart, "CALVES", "TRICEPS" ));
            }
        }
        return trainingList;
    }
}

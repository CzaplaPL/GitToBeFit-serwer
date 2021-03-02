package pl.umk.mat.git2befit.service.workout.factory.implementation;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.conditions.BodyPart;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanInterface;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class CardioTrainingPlan implements TrainingPlanInterface {
    private final String TRAINING_TYPE = "CARDIO";
    private final static int SINGLE_STEP = 3;
    private List<Exercise> allExercises;
    private final ExerciseRepository exerciseRepository;

    public CardioTrainingPlan(ExerciseRepository exerciseRepository) {
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
        List<Exercise> rolledExercises = new ArrayList<>();
        if (filteredListOfExercises.size() <= exercisesToGet) {
            rolledExercises.addAll(filteredListOfExercises);
        } else {
            int counter = 0;
            while (rolledExercises.size() < exercisesToGet &&
                    counter < filteredListOfExercises.size()) {
                int actualRandom = randomIndexGen.nextInt(filteredListOfExercises.size());
                Exercise exercise = filteredListOfExercises.get(actualRandom);
                if (checkIfBodyPartIsNotOverloaded(rolledExercises, exercise)) {
                    rolledExercises.add(exercise);
                }
                counter++;
            }
            for (int i = 0; ((i < filteredListOfExercises.size()) && (rolledExercises.size() != exercisesToGet)); i++) {
                Exercise actualExercise = filteredListOfExercises.get(i);
                if (!rolledExercises.contains(actualExercise)) {
                    rolledExercises.add(actualExercise);
                }
            }
        }
        List<ExerciseExecution> exerciseExecutions = getExercisesExecutions(rolledExercises);
        training.setExercisesExecutions(exerciseExecutions);
        return List.of(training);
    }

    private boolean checkIfBodyPartIsNotOverloaded(List<Exercise> rolledExercises, Exercise exercise) {
        BodyPart bodyPartToFind = exercise.getBodyPart();
        return rolledExercises.stream()
                .map(Exercise::getBodyPart)
                .noneMatch(bodyPart -> bodyPart.equals(bodyPartToFind));

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
                exerciseExecution.setSeries(0);
                exerciseExecution.setCount(3);
                exerciseExecution.setTime(30);
            }
            execList.add(exerciseExecution);
        }
        return execList;
    }
}

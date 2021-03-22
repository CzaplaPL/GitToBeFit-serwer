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
    private final static int MAX_RAND_TRIES = 1000;
    private final ExerciseRepository exerciseRepository;

    public CardioTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public List<Training> create(TrainingForm trainingForm) {
        List<Exercise> allExercises = exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
        List<Exercise> filteredListOfExercises = filterAllByAvailableEquipment(allExercises, trainingForm.getEquipmentIDs());

        int duration = trainingForm.getDuration();
        int exercisesToGet = duration / SINGLE_STEP;
        Training training = new Training();
        List<Exercise> rolledExercises = new ArrayList<>();

        ThreadLocalRandom randomIndexGen = ThreadLocalRandom.current();
        // w przypadku, gdy lista cwiczen jest mniejsza niz wymagana to dodaje wszystko do listy
        if (filteredListOfExercises.size() <= exercisesToGet) {
            rolledExercises.addAll(filteredListOfExercises);
        } else {
            int counter = 0;
            // losowanie ćwiczeń tak, żeby się nie powtarzały
            while (rolledExercises.size() < exercisesToGet && counter < MAX_RAND_TRIES) {
                // losowanie liczby z przedzialu od 0 do filtered.size
                int actualRandom = randomIndexGen.nextInt(filteredListOfExercises.size());
                // sciaganie cwiczenia
                Exercise exercise = filteredListOfExercises.get(actualRandom);
                // test, czy na dana partie ciala nie ma za duzo cwiczen
                if (checkIfBodyPartIsNotOverloaded(rolledExercises, exercise)) {
                    // jesli nie jest to dodajemy cwiczenie
                    rolledExercises.add(exercise);
                    // usuwamy z listy, zeby nie moglo byc wiecej pobrane
                    filteredListOfExercises.remove(actualRandom);
                }
                // licznik obrotow petli
                counter++;
            }
            // wypelnienie listy wylosowanych cwiczen do konca
            while (!filteredListOfExercises.isEmpty() && rolledExercises.size() != exercisesToGet) {
                // losowanie liczby z przedzialu od 0 do filtered.size
                int actualRandom = randomIndexGen.nextInt(filteredListOfExercises.size());
                // sciaganie cwiczenia
                Exercise exercise = filteredListOfExercises.get(actualRandom);
                // dodanie cwiczenia
                rolledExercises.add(exercise);
                // usuniecie cwiczenia z listy pozostalych
                filteredListOfExercises.remove(exercise);
            }
        }

        if (rolledExercises.size() < exercisesToGet) {
            filteredListOfExercises = exerciseRepository.getAllWithNoEquipmentForTrainingTypeName(TRAINING_TYPE);
            while (!filteredListOfExercises.isEmpty() && rolledExercises.size() != exercisesToGet) {
                // losowanie liczby z przedzialu od 0 do filtered.size
                int actualRandom = randomIndexGen.nextInt(filteredListOfExercises.size());
                // sciaganie cwiczenia
                Exercise exercise = filteredListOfExercises.get(actualRandom);
                // dodanie cwiczenia
                if (!rolledExercises.contains(exercise)) {
                    rolledExercises.add(exercise);
                }
                // usuniecie cwiczenia z listy pozostalych
                filteredListOfExercises.remove(exercise);
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

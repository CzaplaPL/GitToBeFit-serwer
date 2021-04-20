package pl.umk.mat.git2befit.service.workout.factory.implementation;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.workout.conditions.BodyPart;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanInterface;
import pl.umk.mat.git2befit.validation.workout.CardioValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CardioTrainingPlan implements TrainingPlanInterface {
    private final String TRAINING_TYPE = "CARDIO";
    private final static int SINGLE_STEP = 3;
    private final static int MAX_RAND_TRIES = 100;
    private final ExerciseRepository exerciseRepository;
    private TrainingForm trainingForm;

    private List<Training> generatedTrainingsList;

    public CardioTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public List<Training> create(TrainingForm trainingForm) {
        List<Exercise> allExercises = exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
        List<Exercise> filteredListOfExercises = filterAllByAvailableEquipment(allExercises, trainingForm.getEquipmentIDs());
        this.trainingForm = trainingForm;
        int duration = trainingForm.getDuration();
        int exercisesToGet = duration / SINGLE_STEP;
        List<Exercise> rolledExercises = new ArrayList<>();

        ThreadLocalRandom randomIndexGen = ThreadLocalRandom.current();
        // w przypadku, gdy lista cwiczen jest mniejsza niz wymagana to dodaje wszystko do listy
        int counter = 0;
        boolean loadingNoEquip = false;
        while(rolledExercises.size() < exercisesToGet) {
            if (!filteredListOfExercises.isEmpty()) {
                int actualRandom = randomIndexGen.nextInt(filteredListOfExercises.size());
                // sciaganie cwiczenia
                Exercise exercise = filteredListOfExercises.get(actualRandom);
                // test, czy na dana partie ciala nie ma za duzo cwiczen

                if (counter < MAX_RAND_TRIES) {
                    if (checkIfBodyPartIsNotOverloaded(rolledExercises, exercise)) {
                        // jesli nie jest to dodajemy cwiczenie
                        if (!rolledExercises.contains(exercise))
                            rolledExercises.add(exercise);
                        // usuwamy z listy, zeby nie moglo byc wiecej pobrane
                        filteredListOfExercises.remove(exercise);
                    } else {
                        counter++;
                    }
                } else {
                    if (!rolledExercises.contains(exercise))
                        rolledExercises.add(exercise);
                    // usuwamy z listy, zeby nie moglo byc wiecej pobrane
                    filteredListOfExercises.remove(exercise);
                }
            } else {
                break;
            }
        }
        List<ExerciseExecution> exerciseExecutions = getExercisesExecutions(rolledExercises);
        Training training = new Training(
                DEFAULT_BREAK_TIME,
                this.trainingForm.checkIfScheduleTypeIsCircuit() ? DEFAULT_CIRCUIT_COUNT : NOT_APPLICABLE,
                exerciseExecutions
        );
        this.generatedTrainingsList = List.of(training);
        return this.generatedTrainingsList;
    }

    @Override
    public void validateAfterCreating() {
        CardioValidator validator = new CardioValidator();
        validator.validate(this.generatedTrainingsList, this.trainingForm);
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
            ExerciseExecution exerciseExecution = getExactExerciseExecution(exercise, this.trainingForm);
            execList.add(exerciseExecution);
        }
        return execList;
    }
}

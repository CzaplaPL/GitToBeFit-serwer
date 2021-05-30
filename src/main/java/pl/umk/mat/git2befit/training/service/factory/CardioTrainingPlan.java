package pl.umk.mat.git2befit.training.service.factory;

import pl.umk.mat.git2befit.training.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.training.model.conditions.BodyPart;
import pl.umk.mat.git2befit.training.model.training.*;
import pl.umk.mat.git2befit.training.repository.ExerciseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class CardioTrainingPlan implements TrainingPlanGenerator {
    private final String TRAINING_TYPE = "CARDIO";
    private final static int SINGLE_STEP = 3;
    private final static int MAX_RAND_TRIES = 100;
    private final ExerciseRepository exerciseRepository;
    private TrainingForm trainingForm;

    public CardioTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public TrainingPlan create(TrainingForm trainingForm) {
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
        return new TrainingPlan(
                this.TRAINING_TYPE,
                trainingForm,
                List.of(training)
        );
    }

    @Override
    public void validate(TrainingPlan trainingPlan, TrainingForm trainingForm) {
        for (Training training : trainingPlan.getPlanList()) {
            // walidacja czasu
            List<ExerciseExecution> exercisesExecutions = training.getExercisesExecutions();

            int size = exercisesExecutions.size();
            if (size * 3 != trainingForm.getDuration())
                throw new NotValidTrainingException("wrong exercises count");

            long count = exercisesExecutions
                    .stream()
                    .map(exerciseExecution -> exerciseExecution.getExercise().getId())
                    .distinct()
                    .count();
            if (count != exercisesExecutions.size())
                throw new NotValidTrainingException("duplicated exercises");
        }
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

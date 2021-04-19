package pl.umk.mat.git2befit.service.workout.factory.implementation;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanInterface;
import pl.umk.mat.git2befit.validation.workout.FitnessValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class FitnessTrainingPlan implements TrainingPlanInterface {
    private static final String TRAINING_TYPE = "FITNESS";
    private static final int SINGLE_STEP = 3;
    private static final List<String> SPECIFIED_ARMS = List.of("SHOULDERS", "TRICEPS", "BICEPS");
    private static final List<String> SPECIFIED_LEGS = List.of("THIGHS", "CALVES");

    private final ExerciseRepository exerciseRepository;
    private TrainingForm trainingForm;
    private List<Training> generatedTraining;

    public FitnessTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public List<Training> create(TrainingForm trainingForm) {
        List<Exercise> allExercises = exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
        List<Exercise> filteredListOfExercises = filterAllByAvailableEquipment(allExercises, trainingForm.getEquipmentIDs());
        this.trainingForm = trainingForm;
        int duration = trainingForm.getDuration();
        int exercisesToGet = duration / SINGLE_STEP;

        ThreadLocalRandom randomIndexGen = ThreadLocalRandom.current();
        List<Exercise> rolledExercises = new ArrayList<>();
        List<String> bodyPartsFromForm = trainingForm.getBodyParts();

        // na kazda partie ciala jest losowane jedno cwiczenie
        while (rolledExercises.size() < exercisesToGet) {
            long counter = 0;
            for (String bodyPart : bodyPartsFromForm) {
                // filtrowanie cwiczen na odpowiednia partie ciala (aktualnie obslugiwana)
                List<Exercise> exercisesForSpecifiedBodyPart = getExercisesForSpecifiedBodyPart(filteredListOfExercises, bodyPart);
                // sprawdzenie, czy na pewno cos jest
                if (!exercisesForSpecifiedBodyPart.isEmpty()) {
                    // randomowa liczba z przedzialu 0 <= liczba < filtrowane.size
                    int random = randomIndexGen.nextInt(exercisesForSpecifiedBodyPart.size());
                    Exercise exercise = exercisesForSpecifiedBodyPart.get(random);
                    // sprawdzenie, czy takiego cwiczenia nie ma w srodku
                    if (!rolledExercises.contains(exercise)) {
                        rolledExercises.add(exercise);
                    }
                    // usuniecie wylosowanego cwiczenia zeby sie nie powtorzylo
                    filteredListOfExercises.remove(exercise);
                } else {
                    counter++;
                }
                if (rolledExercises.size() == exercisesToGet)
                    break;
            }
            // jezeli nie ma cwiczen to wykonaj akcje
            if (filteredListOfExercises.isEmpty()) {
                break;
            } else if (counter == bodyPartsFromForm.size()) {
                break;
            }
        }

        List<ExerciseExecution> exercisesExecutions = getExercisesExecutions(rolledExercises);
        Collections.shuffle(exercisesExecutions);

        Training training = new Training(
                DEFAULT_BREAK_TIME,
                this.trainingForm.checkIfScheduleTypeIsCircuit() ? DEFAULT_CIRCUIT_COUNT : NOT_APPLICABLE,
                exercisesExecutions
        );
        this.generatedTraining = List.of(training);
        return this.generatedTraining;
    }

    @Override
    public void validateAfterCreating() {
        FitnessValidator validator = new FitnessValidator();
        validator.validate(this.generatedTraining, this.trainingForm);
    }

    private List<Exercise> getExercisesForSpecifiedBodyPart(List<Exercise> exercises, String bodyPartName) {
        List<Exercise> filtered = new ArrayList<>();
        switch (bodyPartName) {
            case "LEGS" -> {
                for (String spec : SPECIFIED_LEGS) {
                    List<Exercise> exercisesForBodyPart = exercises.stream()
                            .filter(exercise -> exercise.getBodyPart().getName().toUpperCase().equals(spec))
                            .collect(Collectors.toList());
                    filtered.addAll(exercisesForBodyPart);
                }
                return filtered;
            }
            case "ARMS" -> {
                for (String spec : SPECIFIED_ARMS) {
                    List<Exercise> exercisesForBodyPart = exercises.stream()
                            .filter(exercise -> exercise.getBodyPart().getName().toUpperCase().equals(spec))
                            .collect(Collectors.toList());
                    filtered.addAll(exercisesForBodyPart);
                }
                return filtered;
            }
            default -> {
                return exercises.stream()
                        .filter(exercise -> exercise.getBodyPart().getName().toUpperCase().equals(bodyPartName))
                        .collect(Collectors.toList());
            }
        }
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

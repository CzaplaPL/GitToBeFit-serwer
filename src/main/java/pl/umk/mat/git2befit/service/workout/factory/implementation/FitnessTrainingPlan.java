package pl.umk.mat.git2befit.service.workout.factory.implementation;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanInterface;

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
    private static final List<String> SPECIFIED_ARMS = List.of("SHOULDERS", "TRICEPS", "BICEPS");
    private static final List<String> SPECIFIED_LEGS = List.of("THIGHS", "CALVES");

    private final ExerciseRepository exerciseRepository;

    public FitnessTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public List<Training> create(TrainingForm trainingForm) {
        List<Exercise> allExercises = exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
        List<Exercise> filteredListOfExercises = filterAllByAvailableEquipment(allExercises, trainingForm.getEquipmentIDs());
        int duration = trainingForm.getDuration();
        int exercisesToGet = duration / SINGLE_STEP;

        ThreadLocalRandom randomIndexGen = ThreadLocalRandom.current();
        Training training = new Training();
        List<Exercise> rolledExercises = new ArrayList<>();
        List<String> bodyPartsFromForm;
        // zabezpieczenie, ze jezeli uzytkownik nie przeslal zadnej partii ciala to bedzie generowalo dla wszystkich
        if ((trainingForm.getBodyParts().size() == 1 && trainingForm.getBodyParts().get(0).isEmpty())
                || trainingForm.getBodyParts().isEmpty()) {
            bodyPartsFromForm = ALL_BODY_PARTS;
        } else {
            bodyPartsFromForm = trainingForm.getBodyParts();
        }

        boolean containsNoEquips = false;

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
            }
            // jezeli nie ma cwiczen to wykonaj akcje
            if (filteredListOfExercises.isEmpty()) {
                // sprawdz czy zostaly pobrane cwiczenia bez sprzetu
                if (!containsNoEquips) {
                    filteredListOfExercises.addAll(exerciseRepository.getAllWithNoEquipmentForTrainingTypeName(TRAINING_TYPE));
                    containsNoEquips = true;
                } else {
                    break;
                }
            } else {
                // sprawdzenie czy w puli sa jeszcze jakiekolwiek cwiczenia dla danej partii, jesli nie to wyjscie
                if (counter == bodyPartsFromForm.size()) {
                    break;
                }
            }
        }
        List<ExerciseExecution> exercisesExecutions = getExercisesExecutions(rolledExercises);
        Collections.shuffle(exercisesExecutions);
        training.setExercisesExecutions(exercisesExecutions);
        return List.of(training);
    }

    @Override
    public void validateAfterCreating() {

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

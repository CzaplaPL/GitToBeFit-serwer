package pl.umk.mat.git2befit.service.workout.factory.implementation;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class FitnessTrainingPlan implements TrainingPlanInterface {
    private static final String TRAINING_TYPE = "FITNESS";
    private static final int SINGLE_STEP = 3;
    private static final List<String> ALL_BODY_PARTS = List.of("CHEST", "SIXPACK", "BACK", "LEGS", "ARMS");

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

        // zabezpieczenie, ze jezeli uzytkownik nie przeslal zadnej partii ciala to bedzie generowalo dla wszystkich
        List<String> bodyPartsFromForm = trainingForm.getBodyParts().isEmpty() ? ALL_BODY_PARTS : trainingForm.getBodyParts();

        // licznik, czy na ktoras z partii ciala
        int noExerciseCounter = 0;

        // na kazda partie ciala jest losowane jedno cwiczenie
        for (String bodyPart : bodyPartsFromForm) {
            // filtrowanie cwiczen na odpowiednia partie ciala (aktualnie obslugiwana)
            List<Exercise> exercisesForSpecifiedBodyPart = getExercisesForSpecifiedBodyPart(filteredListOfExercises, bodyPart);
            // sprawdzenie, czy na pewno cos jest
            if (!exercisesForSpecifiedBodyPart.isEmpty()) {
                // randomowa liczba z przedzialu 0 <= liczba < filtrowane.size
                int random = randomIndexGen.nextInt(exercisesForSpecifiedBodyPart.size());
                rolledExercises.add(exercisesForSpecifiedBodyPart.get(random));
                // usuniecie wylosowanego cwiczenia zeby sie nie powtorzylo
                filteredListOfExercises.remove(random);
            } else {
                // oznaczenie, ze brak cwiczen
                noExerciseCounter++;
            }
        }

        // jezeli na zadna partie nie ma cwiczen, to sa pobierane cwiczenia z brakiem sprzetu
        if (noExerciseCounter == bodyPartsFromForm.size()) {
            filteredListOfExercises = exerciseRepository.getAllWithNoEquipment();
        }
        noExerciseCounter = 0;
        int tempIndex = 0;
        // wyrownywanie do odpowiedniego czasu
        // todo czy jezeli brakuje nam np 2 cwiczen do kompletu to tez sciagamy te bez sprzetu?
        // dopoki nie wylosowano odpowiedniej ilosci cwiczen i dopoki sa cwiczenia na jakakolwiek partie ciala
        while (rolledExercises.size() != exercisesToGet && noExerciseCounter != bodyPartsFromForm.size()) {

            // filtrowanie cwiczen na dana partie ciala
            List<Exercise> exercisesForSpecifiedBodyPart = getExercisesForSpecifiedBodyPart(filteredListOfExercises,
                    bodyPartsFromForm.get(tempIndex));
            // test, czy cos sie wylosowalo
            if (!exercisesForSpecifiedBodyPart.isEmpty()) {
                int random = randomIndexGen.nextInt(exercisesForSpecifiedBodyPart.size());
                rolledExercises.add(exercisesForSpecifiedBodyPart.get(random));
                filteredListOfExercises.remove(random);
            } else {
                noExerciseCounter++;
            }
            // sprawdzanie, czy iterator po liscie partii ciala nie wyszedl poza zasieg
            if (tempIndex < bodyPartsFromForm.size()) {
                tempIndex++;
            } else { // reset
                tempIndex = 0;
                noExerciseCounter = 0;
            }
        }
        List<ExerciseExecution> exercisesExecutions = getExercisesExecutions(rolledExercises);
        training.setExercisesExecutions(exercisesExecutions);
        return List.of(training);
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

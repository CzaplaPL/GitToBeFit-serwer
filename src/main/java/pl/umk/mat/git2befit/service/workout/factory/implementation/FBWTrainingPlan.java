package pl.umk.mat.git2befit.service.workout.factory.implementation;

import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanInterface;

import java.util.*;
import java.util.stream.Collectors;


public class FBWTrainingPlan implements TrainingPlanInterface {
    private static final String TRAINING_TYPE = "FBW";
    private static final List<String> bodyPartsList = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS", "SHOULDERS", "CHEST", "BACK", "THIGHS");
    private static final int ONE_DAY = 1;

    private final ExerciseRepository exerciseRepository;

    private List<Exercise> exercisesWithoutEquipment = new ArrayList<>();
    private List<Exercise> exercisesWithEquipment = new ArrayList<>();
    private TrainingForm trainingForm;
    private TrainingForm localTrainingForm;

    public FBWTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    private void initialize(TrainingForm trainingForm) {
        this.trainingForm = trainingForm;
        this.localTrainingForm = trainingForm;
        var exerciseListFilteredByTrainingType = exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
        if (trainingForm.getEquipmentIDs().contains(20L)) {
            localTrainingForm.getEquipmentIDs().remove(20L);
            exercisesWithoutEquipment = exerciseRepository.getAllWithNoEquipmentForTrainingTypeName(TRAINING_TYPE);
        }
        exercisesWithEquipment = filterAllByAvailableEquipment(exerciseListFilteredByTrainingType, trainingForm.getEquipmentIDs());

        // razy daysCount
        if (trainingForm.getScheduleType().equals("REPETITIVE"))
            localTrainingForm.setDaysCount(ONE_DAY);
    }

    @Override
    public List<Training> create(TrainingForm trainingForm) {
        initialize(trainingForm);

        return assignExercisesToBodyPart(exercisesWithEquipment, exercisesWithoutEquipment);
    }

    @Override
    public void validateAfterCreating() {

    }

    private List<Training> assignExercisesToBodyPart(List<Exercise> exercisesWithEquipment, List<Exercise> exercisesWithoutEquipment) {

        List<Training> trainingList = new ArrayList<>();

        var mapOfExercisesByBodyPartForDaysCount = getBodyPartExercisesForDays(exercisesWithEquipment, exercisesWithoutEquipment);

        for (int i = 0; i < localTrainingForm.getDaysCount(); i++) {
            Training training = new Training();
            List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();

            for (String bodyPart : bodyPartsList) {
                var exerciseExecutionsForBodyPart = mapOfExercisesByBodyPartForDaysCount.get(bodyPart);
                //walidacja nie wystarczyło ćwiczeń
                if (exerciseExecutionsForBodyPart.size() != 0)
                    exerciseExecutionList.add(exerciseExecutionsForBodyPart.get(i));
            }
            training.setExercisesExecutions(exerciseExecutionList);
            trainingList.add(training);
        }
        return trainingList;
    }

    private Map<String, List<ExerciseExecution>> getBodyPartExercisesForDays(List<Exercise> exercisesWithEquipment, List<Exercise> exercisesWithoutEquipment) {
        Map<String, List<ExerciseExecution>> exerciseExecutionMap = new HashMap<>();

        for (String bodyPart : bodyPartsList) {
            List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();

            var exercisesWithEquipmentFilteredByBodyPart = getExercisesFilteredByBodyPart(exercisesWithEquipment, bodyPart);
            var exercisesWithoutEquipmentFilteredByBodyPart = getExercisesFilteredByBodyPart(exercisesWithoutEquipment, bodyPart);
            //making some randomizing
            Collections.shuffle(exercisesWithEquipmentFilteredByBodyPart);
            Collections.shuffle(exercisesWithoutEquipmentFilteredByBodyPart);
            //user może wybrać priorytet
            var joinedExercises = new ArrayList<>(exercisesWithEquipmentFilteredByBodyPart);
            joinedExercises.addAll(exercisesWithoutEquipmentFilteredByBodyPart);

            for (int i = 0; i < localTrainingForm.getDaysCount(); i++) {
                ExerciseExecution exerciseExecution = new ExerciseExecution();

                if (isEnough(joinedExercises))
                    exerciseExecution.setExercise(joinedExercises.remove(i));
                else
                    exerciseExecution.setExercise(joinedExercises.get(i % joinedExercises.size()));

                exerciseExecutionList.add(addSeriesAndCount(exerciseExecution));
            }
            exerciseExecutionMap.put(bodyPart, exerciseExecutionList);
        }
        return exerciseExecutionMap;
    }

    private ExerciseExecution addSeriesAndCount(ExerciseExecution exerciseExecution) {
        exerciseExecution.setSeries(3);
        exerciseExecution.setCount(8);
        return exerciseExecution;
    }

    private List<Exercise> getExercisesFilteredByBodyPart(List<Exercise> exercises, String bodyPart) {
        return exercises.stream()
                .filter(exercise -> exercise.getBodyPart().getName().equals(bodyPart))
                .collect(Collectors.toList());
    }

    private boolean isEnough(List<Exercise> exercises) {
        return exercises.size() >= localTrainingForm.getDaysCount();
    }
}

package pl.umk.mat.git2befit.service.workout.factory;

import pl.umk.mat.git2befit.model.workout.training.*;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;
import pl.umk.mat.git2befit.validation.workout.SplitValidator;

import java.util.*;
import java.util.stream.Collectors;

class SplitTrainingPlan implements TrainingPlanGenerator {
    private final String TRAINING_TYPE = "SPLIT";
    private final List<String> smallBodyParts = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS", "SHOULDERS");
    private final List<String> bigBodyParts = List.of("CHEST", "BACK", "THIGHS");
    private final int amountForSmall = 3;
    private final int amountForBig = 4;

    private List<Exercise> exercisesWithEquipment = new ArrayList<>();
    private TrainingForm trainingForm;
    private TrainingForm localTrainingForm;

    private final ExerciseRepository exerciseRepository;

    public SplitTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public void validate(TrainingPlan trainingPlan, TrainingForm trainingForm) {
        //example
        SplitValidator.validateTraining(List.of());
    }

    @Override
    public TrainingPlan create(TrainingForm trainingForm) {
        initialize(trainingForm);

        var trainingForBodyPart = assignExercisesToBodyPart();
        var trainingList = divideTrainingIntoDays(trainingForBodyPart);

        return new TrainingPlan(
                TRAINING_TYPE,
                this.localTrainingForm,
                normalize(trainingList)
        );
    }

    private void initialize(TrainingForm trainingForm) {
        this.trainingForm = trainingForm;
        this.localTrainingForm = trainingForm;

        List<Exercise> exerciseListFilteredByTrainingType = exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
        exercisesWithEquipment = filterAllByAvailableEquipment(exerciseListFilteredByTrainingType, trainingForm.getEquipmentIDs());

        if (trainingForm.getDaysCount() > trainingForm.getBodyParts().size()) {
            var bodyPartsSize = trainingForm.getBodyParts().size();
            localTrainingForm.setDaysCount(bodyPartsSize);
        }
    }

    private Map<String, List<ExerciseExecution>> assignExercisesToBodyPart() {
        var exerciseExecutionList = new ArrayList<ExerciseExecution>();
        var exercisesForBodyPart = new HashMap<String, List<ExerciseExecution>>();

        var trainingFormBodyParts = localTrainingForm.getBodyParts();

        for (String bodyPart : trainingFormBodyParts) {

            var exercisesWithEquipmentFilteredByBodyPart = getExercisesFilteredByBodyPart(exercisesWithEquipment, bodyPart);

            int amountOfExercises = smallBodyParts.contains(bodyPart) ? amountForSmall : amountForBig;

            for (int i = 0; i < amountOfExercises; i++) {
                try {
                    var exerciseExecution = getUniqueExercise(exercisesWithEquipmentFilteredByBodyPart);
                    exerciseExecutionList.add(exerciseExecution);
                } catch (IllegalStateException ignore) {
                }
            }

            exercisesForBodyPart.put(bodyPart, exerciseExecutionList);
            exerciseExecutionList = new ArrayList<>();
        }
        return exercisesForBodyPart;
    }

    private List<Exercise> getExercisesFilteredByBodyPart(List<Exercise> exercises, String bodyPart) {
        return exercises.stream()
                .filter(exercise -> exercise.getBodyPart().getName().equals(bodyPart))
                .collect(Collectors.toList());
    }


    private ExerciseExecution getUniqueExercise(List<Exercise> exercisesWithEquipmentFilteredByBodyPart) throws IllegalStateException {
        var random = new Random();
        int randomExerciseIndex;
        Exercise exercise;

        if (isEnough(exercisesWithEquipmentFilteredByBodyPart)) {
            randomExerciseIndex = random.nextInt(exercisesWithEquipmentFilteredByBodyPart.size());
            exercise = exercisesWithEquipmentFilteredByBodyPart.remove(randomExerciseIndex);
        }else {
            throw new IllegalStateException();
        }

        return getExactExerciseExecution(exercise, this.trainingForm);
    }

    private boolean isEnough(List<Exercise> exercises) {
        return exercises.size() != 0;
    }

    private List<Map<String, List<ExerciseExecution>>> divideTrainingIntoDays(Map<String, List<ExerciseExecution>> exercisesForBodyPart) {
        List<Map<String, List<ExerciseExecution>>> trainingList = new ArrayList<>();

        if (localTrainingForm.getBodyParts().size() == 0)
            return Collections.emptyList();

        switch (localTrainingForm.getDaysCount()) {
            case 1 -> {
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "THIGHS", "TRICEPS", "SHOULDERS0", "CALVES",
                        "CHEST", "BICEPS", "SIXPACK", "BACK"));
            }
            case 2 -> {
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "CHEST", "BICEPS", "SIXPACK", "BACK"));
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "THIGHS", "TRICEPS", "SHOULDERS0", "CALVES"));
            }
            case 3 -> {
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "CHEST", "BICEPS", "SIXPACK"));
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "BACK", "CALVES"));
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "THIGHS", "TRICEPS", "SHOULDERS"));
            }
            case 4 -> {
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "CHEST", "BICEPS"));
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "BACK", "CALVES"));
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "THIGHS", "TRICEPS"));
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "SIXPACK", "SHOULDERS"));
            }
            case 5 -> {
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "CHEST", "BICEPS"));
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "BACK"));
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "THIGHS"));
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "SIXPACK", "SHOULDERS"));
                trainingList.add(getMapOfBodyPartsExercisesForDay(exercisesForBodyPart, "CALVES", "TRICEPS"));
            }
        }

        return trainingList;
    }

    private Map<String, List<ExerciseExecution>> getMapOfBodyPartsExercisesForDay(Map<String, List<ExerciseExecution>> trainingForBodyPart, String... bodyParts) {
        Map<String, List<ExerciseExecution>> map = new HashMap<>();
        for (String part : bodyParts)
            if (trainingForBodyPart.containsKey(part))
                map.put(part, trainingForBodyPart.get(part));

        return map;
    }

    private List<Training> normalize(List<Map<String, List<ExerciseExecution>>> list) {
        var random = new Random();
        int maxIndex, minIndex, min, max;

        do {
            List<Integer> listOfMapsSize = list.stream()
                    .map(stringListMap -> stringListMap.keySet().size())
                    .collect(Collectors.toList());
            maxIndex = 0;
            minIndex = 0;
            min = list.get(0).size();
            max = list.get(0).size();
            for (int i = 1; i < listOfMapsSize.size(); i++) {
                if (listOfMapsSize.get(i) > max) {
                    maxIndex = i;
                    max = listOfMapsSize.get(i);
                }
                if (listOfMapsSize.get(i) < min) {
                    minIndex = i;
                    min = listOfMapsSize.get(i);
                }
            }

            if (max - min > 1) {
                String randomBodyPart = (String) list.get(maxIndex)
                        .keySet()
                        .toArray()[random.nextInt(listOfMapsSize.get(maxIndex))];
                var exerciseExecutions = list.get(maxIndex).get(randomBodyPart);
                list.get(maxIndex).remove(randomBodyPart);
                list.get(minIndex).put(randomBodyPart, exerciseExecutions);
            }
        } while (max - min > 1);


        return parseMapOfExercisesToListOfExercises(list);
    }

    private List<Training> parseMapOfExercisesToListOfExercises(List<Map<String, List<ExerciseExecution>>> list) {
        var trainingList = new ArrayList<Training>();
        list.forEach(trainingDay -> {
            var training = getTrainingForDay(trainingDay);
            training.setCircuitsCount(NOT_APPLICABLE);
            training.setBreakTime(DEFAULT_BREAK_TIME);
            trainingList.add(training);
        });

        return trainingList;
    }

    private Training getTrainingForDay(Map<String, List<ExerciseExecution>> trainingDay) {
        var training = new Training();
        List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();

        trainingDay.keySet()
                .forEach(key -> exerciseExecutionList.addAll(trainingDay.get(key)));

        training.setExercisesExecutions(exerciseExecutionList);
        return training;
    }
}

package pl.umk.mat.git2befit.service.workout.factory;

import pl.umk.mat.git2befit.model.workout.training.*;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanInterface;
import pl.umk.mat.git2befit.validation.workout.FBWValidator;

import java.util.*;
import java.util.stream.Collectors;

class FBWTrainingPlan implements TrainingPlanGenerator {
    private static final String TRAINING_TYPE = "FBW";
    private static final List<String> bodyPartsList = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS", "SHOULDERS", "CHEST", "BACK", "THIGHS");
    private static final int ONE_DAY = 1;

    private final ExerciseRepository exerciseRepository;

    private List<Exercise> exercisesWithEquipment = new ArrayList<>();
    private TrainingForm trainingForm;
    private TrainingForm localTrainingForm;
    private List<Training> trainingList;

    public FBWTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    private void initialize(TrainingForm trainingForm) {
        this.trainingForm = trainingForm;
        this.localTrainingForm = trainingForm;

        var exerciseListFilteredByTrainingType = exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);

        exercisesWithEquipment = filterAllByAvailableEquipment(exerciseListFilteredByTrainingType, trainingForm.getEquipmentIDs());

        if (trainingForm.getScheduleType().equals("REPETITIVE"))
            localTrainingForm.setDaysCount(ONE_DAY);
    }

    @Override
    public TrainingPlan create(TrainingForm trainingForm) {
        initialize(trainingForm);

        return new TrainingPlan(
                TRAINING_TYPE,
                this.localTrainingForm,
                assignExercisesToBodyPart(exercisesWithEquipment)
        );
    }

    @Override
    public void validate() {
        var fbwValidator = new FBWValidator();
        fbwValidator.validateTraining(trainingList);
    }

    private List<Training> assignExercisesToBodyPart(List<Exercise> exercisesWithEquipment) {

        List<Training> trainingList = new ArrayList<>();

        var mapOfExercisesByBodyPartForDaysCount = getBodyPartExercisesForDays(exercisesWithEquipment);

        for (int i = 0; i < localTrainingForm.getDaysCount(); i++) {
            Training training = new Training();
            List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();

            for (String bodyPart : bodyPartsList) {
                var exerciseExecutionsForBodyPart = mapOfExercisesByBodyPartForDaysCount.get(bodyPart);
                if (exerciseExecutionsForBodyPart.size() != 0)
                    exerciseExecutionList.add(exerciseExecutionsForBodyPart.get(i));
            }
            training.setExercisesExecutions(exerciseExecutionList);
            training.setBreakTime(DEFAULT_BREAK_TIME);
            training.setCircuitsCount(NOT_APPLICABLE);

            trainingList.add(training);
        }
        return trainingList;
    }

    private Map<String, List<ExerciseExecution>> getBodyPartExercisesForDays(List<Exercise> exercisesWithEquipment/*, List<Exercise> exercisesWithoutEquipment*/) {
        Map<String, List<ExerciseExecution>> exerciseExecutionMap = new HashMap<>();

        for (String bodyPart : bodyPartsList) {
            List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();

            var exercisesWithEquipmentFilteredByBodyPart = getExercisesFilteredByBodyPart(exercisesWithEquipment, bodyPart);
            //making some randomizing
            Collections.shuffle(exercisesWithEquipmentFilteredByBodyPart);

            for (int i = 0; i < localTrainingForm.getDaysCount(); i++) {
                if(exercisesWithEquipmentFilteredByBodyPart.size() != 0) {
                    Exercise exercise;
                    if (isEnough(exercisesWithEquipmentFilteredByBodyPart))
                        exercise = exercisesWithEquipmentFilteredByBodyPart.remove(i);
                    else
                        exercise = exercisesWithEquipmentFilteredByBodyPart.get(
                                i % exercisesWithEquipmentFilteredByBodyPart.size()
                        );

                    exerciseExecutionList.add(getExactExerciseExecution(
                            exercise,
                            this.trainingForm
                    ));
                }
            }
            exerciseExecutionMap.put(bodyPart, exerciseExecutionList);
        }
        return exerciseExecutionMap;
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

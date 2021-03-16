package pl.umk.mat.git2befit.service.workout.factory.implementation;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanInterface;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FBWTrainingPlan implements TrainingPlanInterface {
    private static final String TRAINING_TYPE = "FBW";
    private static final List<String> bodyPartsList = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS","SHOULDERS", "CHEST", "BACK", "THIGHS");
    private final ExerciseRepository exerciseRepository;
    private TrainingForm trainingForm;

    public FBWTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public List<Training> create(TrainingForm trainingForm) {
        this.trainingForm = trainingForm;
        List<Exercise> exerciseListFilteredByTrainingType = getExerciseListFilteredByTrainingType();
        List<Exercise> exercisesWithoutEquipment = exerciseRepository.getAllWithNoEquipmentForTrainingTypeName(TRAINING_TYPE);
        List<Exercise> exercisesWithEquipment = filterAllByAvailableEquipment(exerciseListFilteredByTrainingType, trainingForm.getEquipmentIDs());
        return assignExercisesToBodyPart(exercisesWithEquipment,exercisesWithoutEquipment);
    }

    private List<Exercise> getExerciseListFilteredByTrainingType(){
        return exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
    }

    private List<Training> assignExercisesToBodyPart(List<Exercise> exercisesWithEquipment, List<Exercise> exercisesWithoutEquipment){

        List<Training> trainingList = new ArrayList<>();

        if(trainingForm.getScheduleType().equals("REPETITIVE")) {
            List<ExerciseExecution> exerciseExecutionList = createExerciseExecutionList(exercisesWithEquipment, exercisesWithoutEquipment);

            Training training = new Training();
            training.setExercisesExecutions(exerciseExecutionList);

            for (int i = 0; i < trainingForm.getDaysCount(); i++)
                trainingList.add(training);
        }else if (trainingForm.getScheduleType().equals("PER_DAY")){
            Map<String, List<ExerciseExecution>> exerciseExecutionMap = getBodyPartExercisesForDays(exercisesWithEquipment, exercisesWithoutEquipment);

            for (int i = 0; i < trainingForm.getDaysCount(); i++) {
                Training training = new Training();
                List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();

                for (String bodyPart: bodyPartsList){
                    List<ExerciseExecution> exerciseExecutionsForBodyPart = exerciseExecutionMap.get(bodyPart);
                    if(exerciseExecutionsForBodyPart.size() != 0)
                        exerciseExecutionList.add(exerciseExecutionsForBodyPart.get(i));
                }
                training.setExercisesExecutions(exerciseExecutionList);
                trainingList.add(training);
            }
        }
        return trainingList;
    }

    private Map<String, List<ExerciseExecution>> getBodyPartExercisesForDays(List<Exercise> exercisesWithEquipment, List<Exercise> exercisesWithoutEquipment) {
        Map<String, List<ExerciseExecution>> exerciseExecutionMap = new HashMap<>();
        for(String bodyPart: bodyPartsList){
            List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();
            for (int i = 0; i < trainingForm.getDaysCount(); i++) {
                ExerciseExecution exerciseExecution = new ExerciseExecution();

                List<Exercise> exercisesWithEquipmentFiltered = getExercisesFilteredByBodyPart(exercisesWithEquipment, bodyPart);
                List<Exercise> exercisesWithoutEquipmentFiltered = getExercisesFilteredByBodyPart(exercisesWithoutEquipment, bodyPart);
                List<Exercise> concatenated = new ArrayList<>(exercisesWithEquipmentFiltered);
                concatenated.addAll(exercisesWithoutEquipmentFiltered);

                if (isEnoughExercises(concatenated))
                    exerciseExecution.setExercise(concatenated.get(i % concatenated.size()));
                exerciseExecution.setSeries(3);
                exerciseExecution.setCount(8);

                if(isEnoughExercises(concatenated))
                    exerciseExecutionList.add(exerciseExecution);
            }
            exerciseExecutionMap.put(bodyPart,exerciseExecutionList);
        }
        return exerciseExecutionMap;
    }

    private List<ExerciseExecution> createExerciseExecutionList(List<Exercise> exercisesWithEquipment, List<Exercise> exercisesWithoutEquipment) {
        List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();
        for (String bodyPart : bodyPartsList) {

            List<Exercise> exercisesWithEquipmentFiltered = getExercisesFilteredByBodyPart(exercisesWithEquipment, bodyPart);
            List<Exercise> exercisesWithoutEquipmentFiltered = getExercisesFilteredByBodyPart(exercisesWithoutEquipment, bodyPart);

            try {
                ExerciseExecution exerciseExecution = getUniqueExercise(exercisesWithEquipmentFiltered, exercisesWithoutEquipmentFiltered);
                exerciseExecutionList.add(exerciseExecution);
            }catch (IllegalArgumentException ignore){}
        }
        return exerciseExecutionList;
    }

    private List<Exercise> getExercisesFilteredByBodyPart(List<Exercise> exercises, String bodyPart) {
        return exercises.stream()
                .filter(exercise -> exercise.getBodyPart().getName().equals(bodyPart))
                .collect(Collectors.toList());
    }

    private ExerciseExecution getUniqueExercise(List<Exercise> exercisesWithEquipmentFiltered, List<Exercise> exercisesWithoutEquipmentFiltered)  throws IllegalStateException{
        Random random = new Random();
        ExerciseExecution exerciseExecution = new ExerciseExecution();
        int randomInt;

        if (isEnoughExercises(exercisesWithEquipmentFiltered)){
            randomInt = random.nextInt(exercisesWithEquipmentFiltered.size());
            exerciseExecution.setExercise(exercisesWithEquipmentFiltered.get(randomInt));
        }else if(isEnoughExercises(exercisesWithoutEquipmentFiltered)){
            randomInt = random.nextInt(exercisesWithoutEquipmentFiltered.size());
            exerciseExecution.setExercise(exercisesWithoutEquipmentFiltered.get(randomInt));
        }else
            throw new IllegalArgumentException();

        exerciseExecution.setSeries(3);
        exerciseExecution.setCount(8);
        return exerciseExecution;
    }

    private boolean isEnoughExercises(List<Exercise> exercises) {
        return exercises.size() != 0;
    }


}

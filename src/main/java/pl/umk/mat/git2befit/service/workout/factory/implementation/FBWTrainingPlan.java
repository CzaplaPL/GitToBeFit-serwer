package pl.umk.mat.git2befit.service.workout.factory.implementation;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.service.workout.factory.TrainingPlanInterface;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.repository.workout.ExerciseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class FBWTrainingPlan implements TrainingPlanInterface {
    private static final String TRAINING_TYPE = "FBW";
    private final ExerciseRepository exerciseRepository;
    private TrainingForm trainingForm;

    public FBWTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public List<Training> create(TrainingForm trainingForm) {
        this.trainingForm = trainingForm;
        List<Exercise> exerciseListFilteredByTrainingType = getExerciseListFilteredByTrainingType();
        List<Exercise> exerciseList = filterAllByAvailableEquipment(exerciseListFilteredByTrainingType, trainingForm.getEquipmentIDs());
        return assignExercisesToBodyPart(exerciseList);
    }

    private List<Exercise> getExerciseListFilteredByTrainingType(){
        return exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
    }

    private List<Training> assignExercisesToBodyPart(List<Exercise> exercises){
        //todo obsłużyć sytuację, że piewszeństwo mają ćwiczenia ze sprzętem w razie braku takich ćwiczeń bierzemy bez sprzętu
        List<String> bodyPartsList = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS","SHOULDER",
                "CHEST", "BACK", "THIGHS");
        Random random = new Random();
        List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();
        List<String> trainingFormBodyParts = trainingForm.getBodyParts();
        List<Training> trainingList = new ArrayList<>();

        if(trainingForm.getScheduleType().equals("REPETITIVE")) {
            for (String s : bodyPartsList) {
                //lista ćwiczeń na daną partię
                List<Exercise> collect = exercises.stream()
                        .filter(exercise -> exercise.getBodyPart().getName().equals(s)).collect(Collectors.toList());
                int i = 0;
                int amountOfExercises = 1;

                //problem kiedy jest za mało ćwiczeń
                if (amountOfExercises <= collect.size()) {
                    ExerciseExecution exerciseExecution = new ExerciseExecution();
                    int randomInt = random.nextInt(collect.size());
                    exerciseExecution.setExercise(collect.get(randomInt));
                    exerciseExecution.setSeries(3);
                    exerciseExecution.setCount(8);
                    exerciseExecutionList.add(exerciseExecution);

                }else{
                    //todo ćwiczenia bez sprzętu dodaj
                }
            }

            Training training = new Training();
            training.setExercisesExecutions(exerciseExecutionList);
            //todo id autogenerowane z bazy
            training.setId(1);
            trainingList.add(training);

        }else if (trainingForm.getScheduleType().equals("PER_DAY")){
            for (int i = 0; i < trainingForm.getDaysCount(); i++) {
                Training training = new Training();

                for (String s : bodyPartsList) {
                    //lista ćwiczeń na daną partię
                    List<Exercise> collect = exercises.stream()
                            .filter(exercise -> exercise.getBodyPart().getName().equals(s)).collect(Collectors.toList());
                    int amountOfExercises = 1;

                    //problem kiedy jest za mało ćwiczeń
                    if (amountOfExercises <= collect.size()) {
                        ExerciseExecution exerciseExecution = new ExerciseExecution();
                        int randomInt = random.nextInt(collect.size());
                        exerciseExecution.setExercise(collect.get(randomInt));
                        exerciseExecution.setSeries(3);
                        exerciseExecution.setCount(8);
                        exerciseExecutionList.add(exerciseExecution);

                    }else{
                        //todo ćwiczenia bez sprzętu dodaj
                    }
                }
                training.setExercisesExecutions(exerciseExecutionList);
                exerciseExecutionList = new ArrayList<>();
                trainingList.add(training);
            }
        }

        return trainingList;
    }
}

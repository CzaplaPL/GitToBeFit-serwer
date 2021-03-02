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
public class SplitTrainingPlan implements TrainingPlanInterface {
    private final String TRAINING_TYPE = "SPLIT";


    private final ExerciseRepository exerciseRepository;

    private TrainingForm trainingForm;


    public SplitTrainingPlan(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }


    @Override
    public List<Training> create(TrainingForm trainingForm) {
        this.trainingForm = trainingForm;
        Training plan = new Training();

        List<Exercise> temp = getExerciseListFilteredByTrainingType();

        List<Exercise> availableExercises = filterAllByAvailableEquipment(temp, trainingForm.getEquipmentIDs());
        List<Training> exerciseExecutions = assignExercisesToBodyPart(availableExercises);

        //typ treningu +
        //filtrowanie sprzętu+
        //dla każdej partii filtruj ćwiczenia z tej partii

        //Przydział ćwiczeń do wybranej partii ciała

        //Podział na dni treningowe


        return exerciseExecutions;
    }

    private List<Exercise> getExerciseListFilteredByTrainingType(){
        return exerciseRepository.getAllByTrainingTypes_Name(TRAINING_TYPE);
    }

    private List<Training> assignExercisesToBodyPart(List<Exercise> exercises){
        //if na każdą partię
        //"CHEST, SIXPACK, BACK, THIGHS, CALVES, BUTTOCKS, BICEPS, TRICEPS, SHOULDERS";

        //todo moze byc wydzielone do klasy ze stalymi
        List<String> smallBodyParts = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS","SHOULDER");
        List<String> bigBodyParts = List.of("CHEST", "BACK", "THIGHS");
        Random random = new Random();
        List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();
        List<String> trainingFormBodyParts = trainingForm.getBodyParts();
        List<Training> trainingList = new ArrayList<>();


        for(String s: trainingFormBodyParts){
            //lista ćwiczeń na daną partię
            List<Exercise> collect = exercises.stream()
                    .filter(exercise -> exercise.getBodyPart().getName().equals(s)).collect(Collectors.toList());
            int i = 0;
            int amountOfExercises = 0;

            if(smallBodyParts.contains(s)) {
                amountOfExercises = 3;
            }else if(bigBodyParts.contains(s)){
               amountOfExercises = 4;
            }

            //problem kiedy jest za mało ćwiczeń
            while (i < amountOfExercises) {
                ExerciseExecution exerciseExecution = new ExerciseExecution();
                int randomInt = random.nextInt(collect.size());
                exerciseExecution.setExercise(collect.get(randomInt));
                exerciseExecution.setSeries(3);
                exerciseExecution.setCount(8);
                collect.remove(randomInt);
                exerciseExecutionList.add(exerciseExecution);
                i++;

                if(i%2 == 0){
                    Training training = new Training();
                    training.setExercisesExecutions(exerciseExecutionList);
                    //todo id autogenerowane z bazy
                    training.setId(1);
                    trainingList.add(training);
                    exerciseExecutionList = new ArrayList<>();
                }

                //Tutaj przypisać po dwa na trainingPlan


            }
        }
        return trainingList;
    }

}

package pl.umk.mat.git2befit.model.training.generation.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.entity.workout.Exercise;
import pl.umk.mat.git2befit.model.entity.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.training.generation.model.ExerciseExecution;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;
import pl.umk.mat.git2befit.repository.ExerciseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
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
    public List<TrainingPlan> create(TrainingForm trainingForm) {
        this.trainingForm = trainingForm;
        TrainingPlan plan = new TrainingPlan();

        List<Exercise> temp = getExerciseListFilteredByTrainingType();

        List<Exercise> availableExercises = filterAllByAvailableEquipment(temp);
        List<TrainingPlan> exerciseExecutions = assignExercisesToBodyPart(availableExercises);

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

    private List<Exercise> filterAllByAvailableEquipment(List<Exercise> exercises){
        List<Long> availableEquipments = trainingForm.getEquipmentIDs();

        return exercises.stream().filter(exercise -> {
            for(Equipment equipment: exercise.getEquipmentsNeeded()) {
                if (!availableEquipments.contains(equipment.getId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private List<TrainingPlan> assignExercisesToBodyPart(List<Exercise> exercises){
        //if na każdą partię
        //"CHEST, SIXPACK, BACK, THIGHS, CALVES, BUTTOCKS, BICEPS, TRICEPS, SHOULDERS";

        //todo moze byc wydzielone do klasy ze stalymi
        List<String> smallBodyParts = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS","SHOULDER");
        List<String> bigBodyParts = List.of("CHEST", "BACK", "THIGHS");
        Random random = new Random();
        List<ExerciseExecution> exerciseExecutionList = new ArrayList<>();
        List<String> trainingFormBodyParts = trainingForm.getBodyParts();
        List<TrainingPlan> trainingPlanList = new ArrayList<>();


        for(String s: trainingFormBodyParts){
            //lista ćwiczeń na daną partię
            List<Exercise> collect = exercises.stream()
                    .filter(exercise -> exercise.getBodyPart().getName().equals(s)).collect(Collectors.toList());
            int i = 0;;
            int amountOfExercises = 0;

            if(smallBodyParts.contains(s)) {
                amountOfExercises = 3;
            }else if(bigBodyParts.contains(s)){
               amountOfExercises = 4;
            }

            //if(smallBodyParts.contains(s)) {
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
                        TrainingPlan trainingPlan = new TrainingPlan();
                        trainingPlan.setExercisesExecutions(exerciseExecutionList);
                        //id autogenerowane z bazy
                        trainingPlan.setId(1);
                        trainingPlanList.add(trainingPlan);
                        exerciseExecutionList = new ArrayList<>();
                    }

                    //Tutaj przypisać po dwa na trainingPlan


                }
            }
       // }
        return trainingPlanList;
    }

}

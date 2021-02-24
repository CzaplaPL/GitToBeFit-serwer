package pl.umk.mat.git2befit.model.training.generation.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.entity.workout.Exercise;
import pl.umk.mat.git2befit.model.entity.workout.conditions.BodyPart;
import pl.umk.mat.git2befit.model.training.generation.model.ExerciseExecution;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;
import pl.umk.mat.git2befit.repository.ExerciseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SplitTrainingPlan implements TrainingPlanInterface {
    private final String TRAINING_TYPE = "SPLIT";
    @Autowired
    private ExerciseRepository exerciseRepository;
    private TrainingForm trainingForm;

    @Override
    public TrainingPlan create(TrainingForm trainingForm) {
        this.trainingForm = trainingForm;
        TrainingPlan plan = new TrainingPlan();

        List<Exercise> temp = getExerciseListFilteredByTrainingType();
        List<Exercise> availableExercises = filterAllByAvailableEquipment(temp);
        //typ treningu +
        //filtrowanie sprzętu+
        //dla każdej partii filtruj ćwiczenia z tej partii

        //Przydział ćwiczeń do wybranej partii ciała
        //Podział na dni treningowe


        return plan;
    }

    private List<Exercise> getExerciseListFilteredByTrainingType(){
        return exerciseRepository.getAllByExerciseForm_Name(TRAINING_TYPE);
    }

    private List<Exercise> filterAllByAvailableEquipment(List<Exercise> exercises){
        List<Long> availableEquipments = trainingForm.getEquipmentIDs();

        Map<Long, Exercise> map = exercises.stream()
                .collect(Collectors.toMap(Exercise::getId, exercise -> exercise));

        return trainingForm.getEquipmentIDs().stream()
                .map(map::get)
                .collect(Collectors.toList());
    }

    private List<ExerciseExecution> assignExercisesToBodyPart(List<Exercise> exercises){
        //if na każdą partię
        //"CHEST, SIXPACK, BACK, THIGHS, CALVES, BUTTOCKS, BICEPS, TRICEPS, SHOULDERS";

        //todo moze byc wydzielone do klasy ze stalymi
        List<String> smallBodyParts = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS","SHOULDER");
        List<String> bigBodyParts = List.of("CHEST", "BACK", "THIGHS");
        Random random = new Random();
        List<ExerciseExecution> traningPlan = new ArrayList<>();
        List<String> bodyParts = trainingForm.getBodyParts();

        for(String s: bodyParts){
            //lista ćwiczeń na daną partię
            List<Exercise> collect = exercises.stream()
                    .filter(exercise -> exercise.getBodyParts().get(0).getName().equals(s)).collect(Collectors.toList());
            int i;
            int amountOfExercices = 0;
            if(smallBodyParts.contains(s)) {
                amountOfExercices = 3;
            }else if(bigBodyParts.contains(s)){
               amountOfExercices = 4;
            }
            if(smallBodyParts.contains(s)) {
                i = 0;
                //problem kiedy jest za mało ćwiczeń
                while (i < amountOfExercices) {
                    ExerciseExecution exerciseExecution = new ExerciseExecution();
                    int randomInt = random.nextInt(collect.size());
                    exerciseExecution.setExercise(collect.get(randomInt));
                    exerciseExecution.setSeries(3);
                    exerciseExecution.setCount(8);
                    collect.remove(randomInt);
                    traningPlan.add(exerciseExecution);
                    i++;
                }
            }
        }
        return traningPlan;
    }

}

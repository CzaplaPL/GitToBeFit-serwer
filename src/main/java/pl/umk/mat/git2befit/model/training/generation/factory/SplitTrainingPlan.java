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
        List<ExerciseExecution> listOfAssignedExercises = assignExercisesToBodyPart(availableExercises);

        //typ treningu +
        //filtrowanie sprzętu+
        //dla każdej partii filtruj ćwiczenia z tej partii+

        //Przydział ćwiczeń do wybranej partii ciała+


        //Podział na dni treningowe

        return plan;
    }

    private List<Exercise> getExerciseListFilteredByTrainingType() {
        return exerciseRepository.getAllByExerciseForm_Name(TRAINING_TYPE);
    }

    private List<Exercise> filterAllByAvailableEquipment(List<Exercise> exercises) {
        List<Long> availableEquipments = trainingForm.getEquipmentIDs();

        Map<Long, Exercise> map = exercises.stream()
                .collect(Collectors.toMap(Exercise::getId, exercise -> exercise));

        return trainingForm.getEquipmentIDs().stream()
                .map(map::get)
                .collect(Collectors.toList());
    }

    private List<ExerciseExecution> assignExercisesToBodyPart(List<Exercise> exercises) {
        //"CHEST, SIXPACK, BACK, THIGHS, CALVES, BUTTOCKS, BICEPS, TRICEPS, SHOULDERS";

        List<String> smallBodyParts = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS", "SHOULDER");
        List<String> bigBodyParts = List.of("CHEST", "BACK", "THIGHS");
        Random random = new Random();
        List<ExerciseExecution> trainingPlan = new ArrayList<>();

        List<String> bodyParts = trainingForm.getBodyParts();

        for (String bodyPart : bodyParts) {
            //lista ćwiczeń na daną partię
            List<Exercise> exercisesListForBodyPart = exercises.stream()
                    .filter(exercise -> exercise.getBodyPart().getName().equals(bodyPart))
                    .collect(Collectors.toList());

            int amountOfExercises, i = 0;


            if (smallBodyParts.contains(bodyPart))
                amountOfExercises = 3;
            else
                amountOfExercises = 4;


            //problem kiedy jest za mało ćwiczeń
            while (i < amountOfExercises) {
                if (amountOfExercises >= exercisesListForBodyPart.size()) {
                    ExerciseExecution exerciseExecution = new ExerciseExecution();
                    int randomInt = random.nextInt(exercisesListForBodyPart.size());

                    exerciseExecution.setExercise(exercisesListForBodyPart.get(randomInt));
                    exercisesListForBodyPart.remove(randomInt);

                    exerciseExecution.setSeries(3);
                    exerciseExecution.setCount(8);

                    trainingPlan.add(exerciseExecution);

                    i++;
                } else {
                    //z brakiem sprzętu
                    //za mało ćwiczeń tego typu w bazie co wtedy ?
                }
            }
        }
        return trainingPlan;
    }

}

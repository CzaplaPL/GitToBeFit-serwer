package pl.umk.mat.git2befit.model.training.generation.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.model.entity.workout.Exercise;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingForm;
import pl.umk.mat.git2befit.model.training.generation.model.TrainingPlan;
import pl.umk.mat.git2befit.repository.ExerciseRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class CardioTrainingPlan implements TrainingPlanInterface {
    private final String TRAINING_TYPE = "CARDIO";
    private final static int SINGLE_STEP = 3;
    private List<Exercise> allExercises;
    @Autowired
    private ExerciseRepository exerciseRepository;

    @Override
    public TrainingPlan create(TrainingForm trainingForm) {
        // sciagnij wszystkie elementy na podstawie typu treningu - git
        // przefiltruj wzgledem odpowiedniego sprzetu - git
        // na podstawie okreslonego czasu wyznacz liczbe cwiczen do wylosowanias
        // wylosowanie odpowiedniej ilosci cwiczen

        this.allExercises = exerciseRepository.getAllByExerciseForm_Name(TRAINING_TYPE);
        List<Exercise> filteredListOfExercises = getFilteredListOfExercises(trainingForm.getEquipmentIDs());

        int duration = trainingForm.getDuration();
        int exercisesToGet = duration / SINGLE_STEP;
        ThreadLocalRandom randomIndexGen = ThreadLocalRandom.current();
        TrainingPlan trainingPlan = new TrainingPlan();
//        List<Exercise>
        for (int i = 0; i < exercisesToGet; i++) {
            int actualRandom = randomIndexGen.nextInt(filteredListOfExercises.size());

        }
        return null;
    }

    private List<Exercise> getFilteredListOfExercises(List<Long> equipmentIndexList) {
        Map<Long, Exercise> exercisesMap = allExercises.stream()
                .collect(Collectors.toMap(Exercise::getId, exercise -> exercise));
        return equipmentIndexList.stream().map(aLong -> exercisesMap.get(aLong)).collect(Collectors.toList());
    }
}

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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
//todo spradzic, czy na pewno uwzglednione jest to, ze trening ma byc obwodowy (chodzi o czas treningu)
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

        //todo podobnie jak u michala, moze byc wydzielone do oddzielnej klasy
        this.allExercises = exerciseRepository.getAllByExerciseForm_Name(TRAINING_TYPE);
        List<Exercise> filteredListOfExercises = getFilteredListOfExercises(trainingForm.getEquipmentIDs());

        int duration = trainingForm.getDuration();
        int exercisesToGet = duration / SINGLE_STEP;
        ThreadLocalRandom randomIndexGen = ThreadLocalRandom.current();
        TrainingPlan trainingPlan = new TrainingPlan();
        List<Exercise> rolledExercises = new ArrayList<>();
        //todo obsluzyc jak jest za malo cwiczen
        //todo brak warunku zakonczenia petli, moze sie nie zakonczyc
        while (rolledExercises.size() < exercisesToGet) {
            int actualRandom = randomIndexGen.nextInt(filteredListOfExercises.size());
            Exercise exercise = filteredListOfExercises.get(actualRandom);
            if (checkIfBodyPartIsNotOverloaded(rolledExercises, exercise)) {
                rolledExercises.add(exercise);
            }
        }
        String scheduleType = trainingForm.getScheduleType().toUpperCase();
        switch (scheduleType) {
            case "SERIES" -> {
                List<ExerciseExecution> exercisesExecutions = getExercisesExecutionsWithSeries(rolledExercises);
                trainingPlan.setExercisesExecutions(exercisesExecutions);
                break;
            }
            case "CIRCUIT" -> {
                List<ExerciseExecution> exerciseExecutions = getExercisesExecutionsInCircuit(rolledExercises);
                trainingPlan.setExercisesExecutions(exerciseExecutions);
                break;
            }
        }
        return trainingPlan;
    }

    private boolean checkIfBodyPartIsNotOverloaded(List<Exercise> rolledExercises, Exercise exercise) {
        //todo abstrakcyjne partie (rece/nogi/plecy) czy dokladne
        List<BodyPart> bodyParts = exercise.getBodyParts();
        for (BodyPart bodyPart : bodyParts) {
            boolean any = rolledExercises.stream().anyMatch(ex -> ex.getBodyParts().contains(bodyPart));
            if (any)
                return true;
        }
        return false;
    }

    //todo do ustalenia, jak zapisujemy w treningu, ile jest obwodow
    private List<ExerciseExecution> getExercisesExecutionsInCircuit(List<Exercise> rolledExercises) {
        List<ExerciseExecution> execList = new ArrayList<>();
        for (Exercise exercise : rolledExercises) {
            String form = exercise.getExerciseForm().getName().toUpperCase();
            ExerciseExecution exerciseExecution = new ExerciseExecution();
            //todo dodac nazwy typow cwiczen
            // powtorzenia
            exerciseExecution.setExercise(exercise);
            if (form.equals("powtorzenia")) {
                exerciseExecution.setSeries(1);
                exerciseExecution.setCount(8);
            } else if (form.equals("czasowy")) {
                exerciseExecution.setTime(2137);
            }
            execList.add(exerciseExecution);
        }
        return execList;
    }

    private List<ExerciseExecution> getExercisesExecutionsWithSeries(List<Exercise> rolledExercises) {
        List<ExerciseExecution> execList = new ArrayList<>();
        for (Exercise exercise : rolledExercises) {
            String form = exercise.getExerciseForm().getName().toUpperCase();
            ExerciseExecution exerciseExecution = new ExerciseExecution();
            //todo dodac nazwy typow cwiczen
            // powtorzenia
            exerciseExecution.setExercise(exercise);
            if (form.equals("powtorzenia")) {
                exerciseExecution.setSeries(3);
                exerciseExecution.setCount(8);
                exerciseExecution.setTime(0);
            } else if (form.equals("czasowy")) {
                exerciseExecution.setSeries(0);
                exerciseExecution.setCount(0);
                exerciseExecution.setTime(2137);
            }
            execList.add(exerciseExecution);
        }
        return execList;
    }

    private List<Exercise> getFilteredListOfExercises(List<Long> equipmentIndexList) {
        Map<Long, Exercise> exercisesMap = allExercises.stream()
                .collect(Collectors.toMap(Exercise::getId, exercise -> exercise));
        return equipmentIndexList.stream().map(aLong -> exercisesMap.get(aLong)).collect(Collectors.toList());
    }
}

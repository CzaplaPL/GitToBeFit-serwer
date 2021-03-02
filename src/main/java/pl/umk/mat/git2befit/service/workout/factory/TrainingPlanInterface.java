package pl.umk.mat.git2befit.service.workout.factory;

import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;

import java.util.List;
import java.util.stream.Collectors;

public interface TrainingPlanInterface {
    List<Training> create(TrainingForm trainingForm);

    default List<Exercise> filterAllByAvailableEquipment(List<Exercise> exercises, List<Long> availableEquipments){
        return exercises.stream().filter(exercise -> {
            for(Equipment equipment: exercise.getEquipmentsNeeded()) {
                if (!availableEquipments.contains(equipment.getId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

}

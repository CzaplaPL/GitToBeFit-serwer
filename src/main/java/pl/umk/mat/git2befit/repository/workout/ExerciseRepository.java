package pl.umk.mat.git2befit.repository.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.umk.mat.git2befit.model.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.workout.training.Exercise;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> getAllByTrainingTypes_Name(String exerciseForm_name);
    @Query(value = "SELECT\n" +
                    "       *\n" +
                    "FROM\n" +
                    "     exercises e\n" +
                    "WHERE e.id in (SELECT\n" +
                    "                     ex_eq.exercise_id\n" +
                    "              from\n" +
                    "                   exercise_equipment ex_eq\n" +
                    "              WHERE\n" +
                    "                    ex_eq.equipment_id IN (SELECT " +
                    "                                               ID " +
                    "                                           FROM" +
                    "                                               equipment equip" +
                    "                                           WHERE " +
                    "                                               equip.name = 'Bez sprzetu'))",
            nativeQuery = true)
    List<Exercise> getAllWithNoEquipment();
}

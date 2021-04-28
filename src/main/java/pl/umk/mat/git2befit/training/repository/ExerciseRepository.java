package pl.umk.mat.git2befit.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.umk.mat.git2befit.training.model.training.Exercise;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> getAllByTrainingTypes_Name(String exerciseForm_name);

    List<Exercise> getAllByBodyPart_NameAndTrainingTypes_Name(String bodyPart, String trainingTypes_name);

    @Query(nativeQuery = true, value =
            """
                SELECT e.*  FROM exercises e
                WHERE e.id in (
                  SELECT a.exercise_id
                  FROM exercise_equipment a INNER JOIN equipment b
                  ON a.equipment_id = b.id
                  WHERE b.name = 'Bez sprzetu'
                )
                AND e.id in (
                  SELECT ttoe.exercise_id
                  FROM training_types_of_exercises ttoe
                  INNER JOIN training_types tt ON tt.id = ttoe.training_type_id
                  WHERE tt.name = :name
                );
            """)
    List<Exercise> getAllWithNoEquipmentForTrainingTypeName(String name);
}

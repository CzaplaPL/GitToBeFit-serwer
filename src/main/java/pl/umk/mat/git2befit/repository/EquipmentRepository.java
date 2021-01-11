package pl.umk.mat.git2befit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.umk.mat.git2befit.model.Entity.workout.equipment.Equipment;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    @Query("SELECT e FROM Equipment e WHERE e.type.id =:id")
    List<Equipment> findAllByEquipmentTypeId(@Param("id") long id);
}

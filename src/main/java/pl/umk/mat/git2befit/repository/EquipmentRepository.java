package pl.umk.mat.git2befit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.model.entity.workout.equipment.Equipment;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findAllByType_Id(long id);
}

package pl.umk.mat.git2befit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.model.entity.workout.equipment.EquipmentType;

public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long> {
}

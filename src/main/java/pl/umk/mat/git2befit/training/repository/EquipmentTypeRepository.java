package pl.umk.mat.git2befit.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.training.model.equipment.EquipmentType;

public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long> {
}

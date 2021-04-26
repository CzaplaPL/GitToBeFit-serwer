package pl.umk.mat.git2befit.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.training.model.equipment.Equipment;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findAllByType_Id(long id);

    Optional<Equipment> findEquipmentByName(String name);
}

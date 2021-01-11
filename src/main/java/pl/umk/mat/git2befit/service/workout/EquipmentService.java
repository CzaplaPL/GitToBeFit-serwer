package pl.umk.mat.git2befit.service.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.entity.workout.equipment.Equipment;
import pl.umk.mat.git2befit.repository.EquipmentRepository;

import java.util.List;

@Service
public class EquipmentService {
    private EquipmentRepository equipmentRepository;

    @Autowired
    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public ResponseEntity<List<Equipment>> getAllEquipments() {
        List<Equipment> all = equipmentRepository.findAll();
        return ResponseEntity.ok(all);
    }

    public ResponseEntity<List<Equipment>> getEquipmentsOfSpecificType(long typeId) {
//        List<Equipment> equipmentList = equipmentRepository.findAllByEquipmentTypeId(typeId);
        List<Equipment> equipmentList = equipmentRepository.findAllByType_Id(typeId);
        return ResponseEntity.ok(equipmentList);
    }
}

package pl.umk.mat.git2befit.service.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.entity.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.simplified.SimplifiedEquipment;
import pl.umk.mat.git2befit.repository.EquipmentRepository;

import java.util.List;
import java.util.stream.Collectors;

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

    public ResponseEntity<List<SimplifiedEquipment>> getEquipmentsOfSpecificType(long typeId) {
        List<Equipment> equipmentList = equipmentRepository.findAllByType_Id(typeId);
        // obiekty equipment sa mapowane do wersji uproszczonej, w ktorej nie ma equipmentType
        List<SimplifiedEquipment> mappedEquipments = equipmentList.stream().map(equipment -> new SimplifiedEquipment(equipment)).collect(Collectors.toList());
        return ResponseEntity.ok(mappedEquipments);
    }
}

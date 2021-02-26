package pl.umk.mat.git2befit.service.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.entity.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.simplified.SimplifiedEquipment;
import pl.umk.mat.git2befit.repository.EquipmentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.umk.mat.git2befit.model.entity.workout.equipment.ServerLocationConstraints.EQUIPMENT_PHOTO_PREFIX;

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
        List<SimplifiedEquipment> mappedEquipments = equipmentList.stream().map(equipment -> {
            String url = equipment.getUrl();
            equipment.setUrl(String.join("", EQUIPMENT_PHOTO_PREFIX, url));
            return new SimplifiedEquipment(equipment);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(mappedEquipments);
    }

    public ResponseEntity<?> getNoEquipment() {
        Optional<Equipment> noEquipOptional = equipmentRepository.findEquipmentByName("Bez sprzetu");
        if (noEquipOptional.isPresent()) {
            Equipment noEquip = noEquipOptional.get();
            return ResponseEntity.ok().header("id", String.valueOf(noEquip.getId())).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Cause", "doesnt exist").build();
        }
    }
}

package pl.umk.mat.git2befit.service.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.workout.equipment.EquipmentType;
import pl.umk.mat.git2befit.repository.workout.EquipmentTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

import static pl.umk.mat.git2befit.model.workout.equipment.ServerLocationConstraints.EQUIPMENT_TYPE_PHOTO_PREFIX;

@Service
public class EquipmentTypeService {
    private final EquipmentTypeRepository equipmentTypeRepository;

    @Autowired
    public EquipmentTypeService(EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    public ResponseEntity<List<EquipmentType>> getAllEquipmentTypes() {
        List<EquipmentType> all = equipmentTypeRepository.findAll();
        all = all.stream().map(equipmentType -> {
                String url = equipmentType.getUrl();
                equipmentType.setUrl(String.join("", EQUIPMENT_TYPE_PHOTO_PREFIX, url));
                return equipmentType;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(all);
    }


}

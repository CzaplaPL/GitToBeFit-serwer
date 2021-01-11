package pl.umk.mat.git2befit.service.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.Entity.workout.equipment.EquipmentType;
import pl.umk.mat.git2befit.repository.EquipmentTypeRepository;

import java.util.List;

@Service
public class EquipmentTypeService {
    private EquipmentTypeRepository equipmentTypeRepository;

    @Autowired
    public EquipmentTypeService(EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    public ResponseEntity<List<EquipmentType>> getAllEquipmentTypes() {
        List<EquipmentType> all = equipmentTypeRepository.findAll();
        return ResponseEntity.ok(all);
    }
}

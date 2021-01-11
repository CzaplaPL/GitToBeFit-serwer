package pl.umk.mat.git2befit.controller.workout;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.model.Entity.workout.equipment.EquipmentType;
import pl.umk.mat.git2befit.service.workout.EquipmentTypeService;

import java.util.List;

@RestController
@RequestMapping("/equipment-type")
public class EquipmentTypeController {
    private EquipmentTypeService equipmentTypeService;

    public EquipmentTypeController(EquipmentTypeService equipmentTypeService) {
        this.equipmentTypeService = equipmentTypeService;
    }

    @GetMapping()
    public ResponseEntity<List<EquipmentType>> getAllEquipmentTypes() {
        return equipmentTypeService.getAllEquipmentTypes();
    }
}
